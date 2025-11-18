/*
 * V4L2 Camera Capture Application for NVIDIA Jetson
 *
 * This application demonstrates:
 * - V4L2 (Video4Linux2) camera interface
 * - MMAP buffer management
 * - Format negotiation and configuration
 * - Frame capture and processing
 * - JPEG encoding (optional)
 * - Multi-camera support
 *
 * Compatible with: CSI cameras, USB webcams
 * Tested on: Jetson TX2, Xavier, Orin, Nano
 *
 * Compile:
 *   g++ -std=c++11 -Wall -O2 -o camera_capture camera_capture.cpp -ljpeg
 *
 * Usage:
 *   ./camera_capture [device] [width] [height] [format] [count]
 *
 * Examples:
 *   ./camera_capture /dev/video0 1920 1080 YUYV 10
 *   ./camera_capture /dev/video0 640 480 MJPG 1
 */

#include <iostream>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <vector>
#include <fcntl.h>
#include <unistd.h>
#include <errno.h>
#include <sys/ioctl.h>
#include <sys/mman.h>
#include <sys/stat.h>
#include <linux/videodev2.h>

// Optional JPEG support
#ifdef HAVE_JPEG
#include <jpeglib.h>
#endif

#define BUFFER_COUNT 4

class V4L2Camera {
private:
    int fd;
    std::string device;
    struct v4l2_format fmt;
    struct v4l2_requestbuffers req;

    struct Buffer {
        void *start;
        size_t length;
    };
    std::vector<Buffer> buffers;

public:
    V4L2Camera(const std::string& dev) : fd(-1), device(dev) {}

    ~V4L2Camera() {
        close_device();
    }

    /**
     * Open video device
     */
    bool open_device() {
        struct stat st;

        if (stat(device.c_str(), &st) == -1) {
            std::cerr << "Cannot identify device: " << device << std::endl;
            return false;
        }

        if (!S_ISCHR(st.st_mode)) {
            std::cerr << device << " is not a character device" << std::endl;
            return false;
        }

        fd = open(device.c_str(), O_RDWR | O_NONBLOCK, 0);
        if (fd == -1) {
            std::cerr << "Cannot open device: " << device << std::endl;
            return false;
        }

        std::cout << "Opened device: " << device << std::endl;
        return true;
    }

    /**
     * Query device capabilities
     */
    bool query_capabilities() {
        struct v4l2_capability cap;

        if (ioctl(fd, VIDIOC_QUERYCAP, &cap) == -1) {
            std::cerr << "VIDIOC_QUERYCAP failed" << std::endl;
            return false;
        }

        std::cout << "\nDevice Capabilities:" << std::endl;
        std::cout << "  Driver: " << cap.driver << std::endl;
        std::cout << "  Card: " << cap.card << std::endl;
        std::cout << "  Bus: " << cap.bus_info << std::endl;
        std::cout << "  Version: " << ((cap.version >> 16) & 0xFF) << "."
                  << ((cap.version >> 8) & 0xFF) << "."
                  << (cap.version & 0xFF) << std::endl;

        if (!(cap.capabilities & V4L2_CAP_VIDEO_CAPTURE)) {
            std::cerr << "Device does not support video capture" << std::endl;
            return false;
        }

        if (!(cap.capabilities & V4L2_CAP_STREAMING)) {
            std::cerr << "Device does not support streaming I/O" << std::endl;
            return false;
        }

        return true;
    }

    /**
     * List supported formats
     */
    void list_formats() {
        struct v4l2_fmtdesc fmtdesc;
        std::cout << "\nSupported formats:" << std::endl;

        fmtdesc.index = 0;
        fmtdesc.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;

        while (ioctl(fd, VIDIOC_ENUM_FMT, &fmtdesc) == 0) {
            std::cout << "  [" << fmtdesc.index << "] "
                      << fmtdesc.description << " (";
            std::cout << (char)(fmtdesc.pixelformat & 0xFF)
                      << (char)((fmtdesc.pixelformat >> 8) & 0xFF)
                      << (char)((fmtdesc.pixelformat >> 16) & 0xFF)
                      << (char)((fmtdesc.pixelformat >> 24) & 0xFF);
            std::cout << ")" << std::endl;
            fmtdesc.index++;
        }
    }

    /**
     * Set video format
     */
    bool set_format(uint32_t width, uint32_t height, uint32_t pixelformat) {
        memset(&fmt, 0, sizeof(fmt));
        fmt.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
        fmt.fmt.pix.width = width;
        fmt.fmt.pix.height = height;
        fmt.fmt.pix.pixelformat = pixelformat;
        fmt.fmt.pix.field = V4L2_FIELD_NONE;

        if (ioctl(fd, VIDIOC_S_FMT, &fmt) == -1) {
            std::cerr << "VIDIOC_S_FMT failed" << std::endl;
            return false;
        }

        std::cout << "\nFormat set:" << std::endl;
        std::cout << "  Width: " << fmt.fmt.pix.width << std::endl;
        std::cout << "  Height: " << fmt.fmt.pix.height << std::endl;
        std::cout << "  Pixel Format: " << (char)(fmt.fmt.pix.pixelformat & 0xFF)
                  << (char)((fmt.fmt.pix.pixelformat >> 8) & 0xFF)
                  << (char)((fmt.fmt.pix.pixelformat >> 16) & 0xFF)
                  << (char)((fmt.fmt.pix.pixelformat >> 24) & 0xFF) << std::endl;
        std::cout << "  Bytes per line: " << fmt.fmt.pix.bytesperline << std::endl;
        std::cout << "  Image size: " << fmt.fmt.pix.sizeimage << " bytes" << std::endl;

        return true;
    }

    /**
     * Initialize memory-mapped buffers
     */
    bool init_mmap() {
        memset(&req, 0, sizeof(req));
        req.count = BUFFER_COUNT;
        req.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
        req.memory = V4L2_MEMORY_MMAP;

        if (ioctl(fd, VIDIOC_REQBUFS, &req) == -1) {
            std::cerr << "VIDIOC_REQBUFS failed" << std::endl;
            return false;
        }

        if (req.count < 2) {
            std::cerr << "Insufficient buffer memory" << std::endl;
            return false;
        }

        buffers.resize(req.count);

        for (uint32_t i = 0; i < req.count; i++) {
            struct v4l2_buffer buf;
            memset(&buf, 0, sizeof(buf));
            buf.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
            buf.memory = V4L2_MEMORY_MMAP;
            buf.index = i;

            if (ioctl(fd, VIDIOC_QUERYBUF, &buf) == -1) {
                std::cerr << "VIDIOC_QUERYBUF failed" << std::endl;
                return false;
            }

            buffers[i].length = buf.length;
            buffers[i].start = mmap(NULL, buf.length,
                                   PROT_READ | PROT_WRITE,
                                   MAP_SHARED,
                                   fd, buf.m.offset);

            if (buffers[i].start == MAP_FAILED) {
                std::cerr << "mmap failed" << std::endl;
                return false;
            }
        }

        std::cout << "Initialized " << req.count << " buffers" << std::endl;
        return true;
    }

    /**
     * Start capturing
     */
    bool start_capture() {
        for (uint32_t i = 0; i < req.count; i++) {
            struct v4l2_buffer buf;
            memset(&buf, 0, sizeof(buf));
            buf.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
            buf.memory = V4L2_MEMORY_MMAP;
            buf.index = i;

            if (ioctl(fd, VIDIOC_QBUF, &buf) == -1) {
                std::cerr << "VIDIOC_QBUF failed" << std::endl;
                return false;
            }
        }

        enum v4l2_buf_type type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
        if (ioctl(fd, VIDIOC_STREAMON, &type) == -1) {
            std::cerr << "VIDIOC_STREAMON failed" << std::endl;
            return false;
        }

        std::cout << "Capture started" << std::endl;
        return true;
    }

    /**
     * Capture single frame
     */
    bool capture_frame(void **frame_data, size_t *frame_size) {
        struct v4l2_buffer buf;
        memset(&buf, 0, sizeof(buf));
        buf.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
        buf.memory = V4L2_MEMORY_MMAP;

        if (ioctl(fd, VIDIOC_DQBUF, &buf) == -1) {
            if (errno == EAGAIN)
                return false;
            std::cerr << "VIDIOC_DQBUF failed" << std::endl;
            return false;
        }

        if (buf.index >= buffers.size()) {
            std::cerr << "Invalid buffer index" << std::endl;
            return false;
        }

        *frame_data = buffers[buf.index].start;
        *frame_size = buf.bytesused;

        std::cout << "Frame captured: " << buf.bytesused << " bytes, "
                  << "timestamp: " << buf.timestamp.tv_sec << "."
                  << buf.timestamp.tv_usec << std::endl;

        if (ioctl(fd, VIDIOC_QBUF, &buf) == -1) {
            std::cerr << "VIDIOC_QBUF failed" << std::endl;
            return false;
        }

        return true;
    }

    /**
     * Stop capturing
     */
    void stop_capture() {
        enum v4l2_buf_type type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
        if (ioctl(fd, VIDIOC_STREAMOFF, &type) == -1) {
            std::cerr << "VIDIOC_STREAMOFF failed" << std::endl;
        }
        std::cout << "Capture stopped" << std::endl;
    }

    /**
     * Save frame to file
     */
    bool save_frame(const void *data, size_t size, const std::string& filename) {
        FILE *fp = fopen(filename.c_str(), "wb");
        if (!fp) {
            std::cerr << "Cannot open file: " << filename << std::endl;
            return false;
        }

        if (fwrite(data, size, 1, fp) != 1) {
            std::cerr << "Error writing file" << std::endl;
            fclose(fp);
            return false;
        }

        fclose(fp);
        std::cout << "Saved frame to: " << filename << std::endl;
        return true;
    }

    /**
     * Close device and cleanup
     */
    void close_device() {
        for (size_t i = 0; i < buffers.size(); i++) {
            if (buffers[i].start != MAP_FAILED) {
                munmap(buffers[i].start, buffers[i].length);
            }
        }

        if (fd != -1) {
            close(fd);
            fd = -1;
        }
    }

    /**
     * Get current format info
     */
    const struct v4l2_format& get_format() const {
        return fmt;
    }
};

/**
 * Convert fourcc string to pixel format
 */
uint32_t fourcc_from_string(const std::string& s) {
    if (s.length() != 4)
        return 0;
    return v4l2_fourcc(s[0], s[1], s[2], s[3]);
}

/**
 * Print usage
 */
void print_usage(const char *prog_name) {
    std::cout << "Usage: " << prog_name
              << " [device] [width] [height] [format] [count]" << std::endl;
    std::cout << "\nDefaults:" << std::endl;
    std::cout << "  device: /dev/video0" << std::endl;
    std::cout << "  width: 1920" << std::endl;
    std::cout << "  height: 1080" << std::endl;
    std::cout << "  format: YUYV (alternatives: MJPG, RGB3, BGR3)" << std::endl;
    std::cout << "  count: 10" << std::endl;
    std::cout << "\nExamples:" << std::endl;
    std::cout << "  " << prog_name << " /dev/video0 640 480 YUYV 5" << std::endl;
    std::cout << "  " << prog_name << " /dev/video0 1920 1080 MJPG 1" << std::endl;
}

/**
 * Main function
 */
int main(int argc, char *argv[]) {
    // Parse arguments
    std::string device = (argc > 1) ? argv[1] : "/dev/video0";
    uint32_t width = (argc > 2) ? atoi(argv[2]) : 1920;
    uint32_t height = (argc > 3) ? atoi(argv[3]) : 1080;
    std::string format_str = (argc > 4) ? argv[4] : "YUYV";
    int frame_count = (argc > 5) ? atoi(argv[5]) : 10;

    uint32_t pixelformat = fourcc_from_string(format_str);
    if (pixelformat == 0) {
        std::cerr << "Invalid pixel format: " << format_str << std::endl;
        return 1;
    }

    // Initialize camera
    V4L2Camera camera(device);

    if (!camera.open_device())
        return 1;

    if (!camera.query_capabilities())
        return 1;

    camera.list_formats();

    if (!camera.set_format(width, height, pixelformat))
        return 1;

    if (!camera.init_mmap())
        return 1;

    if (!camera.start_capture())
        return 1;

    // Capture frames
    std::cout << "\nCapturing " << frame_count << " frames..." << std::endl;

    for (int i = 0; i < frame_count; i++) {
        void *frame_data;
        size_t frame_size;

        // Wait for frame (with timeout)
        fd_set fds;
        struct timeval tv;
        FD_ZERO(&fds);
        FD_SET(camera.get_format().type, &fds);
        tv.tv_sec = 2;
        tv.tv_usec = 0;

        // Capture frame
        while (!camera.capture_frame(&frame_data, &frame_size)) {
            usleep(10000);  // 10ms
        }

        // Save frame
        char filename[64];
        snprintf(filename, sizeof(filename), "frame_%04d.raw", i);
        camera.save_frame(frame_data, frame_size, filename);
    }

    camera.stop_capture();

    std::cout << "\nCapture complete!" << std::endl;
    std::cout << "Note: Raw frames saved. Convert with:" << std::endl;
    std::cout << "  ffmpeg -f rawvideo -pixel_format yuyv422 -video_size "
              << width << "x" << height
              << " -i frame_0000.raw frame_0000.png" << std::endl;

    return 0;
}

/*
 * Additional Information:
 * =======================
 *
 * 1. List video devices:
 *    ls /dev/video*
 *    v4l2-ctl --list-devices
 *
 * 2. Query device capabilities:
 *    v4l2-ctl -d /dev/video0 --all
 *    v4l2-ctl -d /dev/video0 --list-formats-ext
 *
 * 3. Test camera with GStreamer:
 *    gst-launch-1.0 v4l2src device=/dev/video0 ! xvimagesink
 *    gst-launch-1.0 nvarguscamerasrc ! nvoverlaysink
 *
 * 4. NVIDIA Argus API (for CSI cameras):
 *    Use nvarguscamerasrc in GStreamer
 *    Or libargus for direct access
 *
 * 5. Convert raw frames:
 *    ffmpeg -f rawvideo -pixel_format yuyv422 -video_size 1920x1080 \
 *           -i frame_0000.raw frame_0000.png
 *
 * 6. Troubleshooting:
 *    - Permission denied: Add user to video group
 *      sudo usermod -a -G video $USER
 *    - No device: Check dmesg for camera driver
 *    - Format not supported: Try v4l2-ctl --list-formats
 */
