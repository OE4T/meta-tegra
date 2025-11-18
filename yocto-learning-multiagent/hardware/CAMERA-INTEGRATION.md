# Camera Integration Guide

## Overview

Comprehensive guide for integrating cameras with NVIDIA Jetson platforms, covering CSI cameras, USB cameras, V4L2, GStreamer, ISP tuning, and multi-camera configurations.

## Camera Interfaces

### CSI (Camera Serial Interface)

**MIPI CSI-2 Support:**
- High-bandwidth camera interface
- Direct connection to ISP
- Low latency
- Hardware-accelerated processing

**Platform Capabilities:**

| Platform | CSI Lanes | Max Cameras | Max Resolution | Max Bandwidth |
|----------|-----------|-------------|----------------|---------------|
| AGX Orin | 32 | 16 | 8K30 | ~60 Gbps |
| Orin NX 16GB | 24 | 6 | 8K30 | ~45 Gbps |
| Orin Nano | 16 | 4 | 8K30 | ~30 Gbps |
| AGX Xavier | 32 | 16 | 8K30 | ~60 Gbps |
| Xavier NX | 16 | 6 | 6K | ~30 Gbps |
| Nano | 8 | 2 | 4K30 | ~15 Gbps |

### USB Cameras

**USB Support:**
- USB 2.0: Up to 480 Mbps (suitable for 720p/1080p)
- USB 3.0/3.1: Up to 5-10 Gbps (4K capable)
- UVC (USB Video Class) standard support
- Plug-and-play with V4L2

**Advantages:**
- Easy integration
- Wide device compatibility
- Hot-pluggable

**Limitations:**
- Higher CPU overhead
- Higher latency than CSI
- Limited to USB bandwidth

## CSI Camera Hardware

### Connector Pinout

**Standard 15-pin CSI Connector:**
```
Pin  Signal         Pin  Signal
1    GND            2    CSI_D0_N
3    CSI_D0_P       4    GND
5    CSI_D1_N       6    CSI_D1_P
7    GND            8    CSI_CLK_N
9    CSI_CLK_P      10   GND
11   CSI_D2_N       12   CSI_D2_P
13   GND            14   CSI_D3_N
15   CSI_D3_P
```

**Additional Signals (on some connectors):**
- I2C_SDA/SCL (camera control)
- GPIO (power enable, reset)
- 3.3V/5V power

### Lane Configuration

**2-lane Camera:**
- Uses D0, D1, CLK
- Up to 1.5 Gbps per lane (CSI-2 v1.1)
- Supports up to ~150 MP/s throughput

**4-lane Camera:**
- Uses D0, D1, D2, D3, CLK
- Up to 1.5 Gbps per lane
- Supports up to ~300 MP/s throughput

### Supported Camera Modules

#### NVIDIA Reference Cameras

**IMX219 (Raspberry Pi Camera v2)**
- Sensor: Sony IMX219
- Resolution: 8MP (3280x2464)
- Framerate: 30fps @ 8MP, 60fps @ 1080p
- Interface: 2-lane CSI
- Cost: ~$25

**IMX477 (Raspberry Pi HQ Camera)**
- Sensor: Sony IMX477
- Resolution: 12.3MP (4056x3040)
- Framerate: 30fps @ 12MP
- Interface: 2-lane CSI
- C/CS-mount for interchangeable lenses
- Cost: ~$50

**IMX274**
- Sensor: Sony IMX274
- Resolution: 8.51MP (3840x2160)
- Framerate: 60fps @ 4K
- Interface: 4-lane CSI
- HDR support

#### E-CON Systems Cameras

**e-CAM130_CUXVR**
- Sensor: AR1335 (13MP)
- Resolution: 4160x3120
- Interface: 2/4-lane CSI
- Automotive-grade

**See3CAM_CU135**
- Sensor: AR0135 (1.2MP)
- Resolution: 1280x960
- High frame rate (up to 120fps @ 720p)
- Global shutter

#### Leopard Imaging Cameras

**LI-IMX390-FPDLINKIII**
- Sensor: Sony IMX390
- Automotive HDR
- FPDLINK III interface
- Temperature range: -40°C to 85°C

### Camera Carrier Boards

**Auvidea J20/J120**
- Multiple CSI camera inputs
- Compact form factor
- Level shifters included

**RidgeRun Camera Expansion**
- Up to 16 cameras (AGX platforms)
- Synchronized capture
- Custom ISP configuration

## V4L2 (Video for Linux 2)

### V4L2 Architecture

```
┌─────────────┐
│ Application │
└──────┬──────┘
       │ V4L2 API
┌──────▼──────┐
│   V4L2      │
│  Subsystem  │
└──────┬──────┘
       │
┌──────▼──────┐     ┌──────────┐
│   Camera    │────▶│   ISP    │
│   Driver    │     │ (Tegra)  │
└─────────────┘     └──────────┘
```

### Device Discovery

```bash
# List V4L2 devices
v4l2-ctl --list-devices

# Typical output for CSI camera:
# vi-output, lt6911uxc 10-0056 (platform:tegra-capture-vi):
#     /dev/video0

# USB camera:
# USB 2.0 Camera (usb-3610000.xhci-2.1):
#     /dev/video1
```

### Device Information

```bash
# Get device capabilities
v4l2-ctl -d /dev/video0 --all

# List supported formats
v4l2-ctl -d /dev/video0 --list-formats-ext

# Get current format
v4l2-ctl -d /dev/video0 --get-fmt-video
```

### Camera Controls

```bash
# List all controls
v4l2-ctl -d /dev/video0 --list-ctrls

# Common controls:
# - brightness
# - contrast
# - saturation
# - hue
# - white_balance_temperature_auto
# - exposure_auto
# - gain

# Set control value
v4l2-ctl -d /dev/video0 --set-ctrl=brightness=128
v4l2-ctl -d /dev/video0 --set-ctrl=exposure_auto=1
v4l2-ctl -d /dev/video0 --set-ctrl=gain=50
```

### Capture Formats

```bash
# Set format (1920x1080, YUYV)
v4l2-ctl -d /dev/video0 --set-fmt-video=width=1920,height=1080,pixelformat=YUYV

# Common pixel formats:
# - YUYV (YUV 4:2:2)
# - MJPG (Motion JPEG)
# - H264 (H.264 encoded)
# - RG10 (RAW Bayer 10-bit)
# - NV12 (YUV 4:2:0)
```

### V4L2 Capture Example (C)

```c
#include <fcntl.h>
#include <sys/ioctl.h>
#include <linux/videodev2.h>
#include <sys/mman.h>
#include <string.h>
#include <stdio.h>

#define DEVICE "/dev/video0"
#define WIDTH 1920
#define HEIGHT 1080

struct buffer {
    void *start;
    size_t length;
};

int main() {
    int fd;
    struct v4l2_format fmt;
    struct v4l2_requestbuffers req;
    struct v4l2_buffer buf;
    struct buffer *buffers;
    unsigned int n_buffers;

    // Open device
    fd = open(DEVICE, O_RDWR);
    if (fd == -1) {
        perror("open");
        return 1;
    }

    // Set format
    memset(&fmt, 0, sizeof(fmt));
    fmt.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
    fmt.fmt.pix.width = WIDTH;
    fmt.fmt.pix.height = HEIGHT;
    fmt.fmt.pix.pixelformat = V4L2_PIX_FMT_YUYV;
    fmt.fmt.pix.field = V4L2_FIELD_NONE;

    if (ioctl(fd, VIDIOC_S_FMT, &fmt) == -1) {
        perror("VIDIOC_S_FMT");
        return 1;
    }

    // Request buffers
    memset(&req, 0, sizeof(req));
    req.count = 4;
    req.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
    req.memory = V4L2_MEMORY_MMAP;

    if (ioctl(fd, VIDIOC_REQBUFS, &req) == -1) {
        perror("VIDIOC_REQBUFS");
        return 1;
    }

    buffers = calloc(req.count, sizeof(*buffers));

    // Map buffers
    for (n_buffers = 0; n_buffers < req.count; n_buffers++) {
        memset(&buf, 0, sizeof(buf));
        buf.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
        buf.memory = V4L2_MEMORY_MMAP;
        buf.index = n_buffers;

        if (ioctl(fd, VIDIOC_QUERYBUF, &buf) == -1) {
            perror("VIDIOC_QUERYBUF");
            return 1;
        }

        buffers[n_buffers].length = buf.length;
        buffers[n_buffers].start = mmap(NULL, buf.length,
                                        PROT_READ | PROT_WRITE,
                                        MAP_SHARED, fd, buf.m.offset);

        if (buffers[n_buffers].start == MAP_FAILED) {
            perror("mmap");
            return 1;
        }
    }

    // Queue buffers
    for (unsigned int i = 0; i < n_buffers; i++) {
        memset(&buf, 0, sizeof(buf));
        buf.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
        buf.memory = V4L2_MEMORY_MMAP;
        buf.index = i;

        if (ioctl(fd, VIDIOC_QBUF, &buf) == -1) {
            perror("VIDIOC_QBUF");
            return 1;
        }
    }

    // Start streaming
    enum v4l2_buf_type type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
    if (ioctl(fd, VIDIOC_STREAMON, &type) == -1) {
        perror("VIDIOC_STREAMON");
        return 1;
    }

    // Capture frame
    memset(&buf, 0, sizeof(buf));
    buf.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
    buf.memory = V4L2_MEMORY_MMAP;

    if (ioctl(fd, VIDIOC_DQBUF, &buf) == -1) {
        perror("VIDIOC_DQBUF");
        return 1;
    }

    // Process frame (buffers[buf.index].start contains frame data)
    printf("Captured frame: %d bytes\n", buf.bytesused);

    // Requeue buffer
    if (ioctl(fd, VIDIOC_QBUF, &buf) == -1) {
        perror("VIDIOC_QBUF");
        return 1;
    }

    // Stop streaming
    if (ioctl(fd, VIDIOC_STREAMOFF, &type) == -1) {
        perror("VIDIOC_STREAMOFF");
        return 1;
    }

    // Cleanup
    for (unsigned int i = 0; i < n_buffers; i++)
        munmap(buffers[i].start, buffers[i].length);
    free(buffers);
    close(fd);

    return 0;
}
```

## GStreamer Pipelines

### Basic Pipelines

**Test Pattern:**
```bash
gst-launch-1.0 videotestsrc ! xvimagesink
```

**CSI Camera Preview:**
```bash
# Using nvarguscamerasrc (NVIDIA Argus Camera)
gst-launch-1.0 nvarguscamerasrc ! 'video/x-raw(memory:NVMM),width=1920,height=1080,framerate=30/1' ! nvvidconv ! xvimagesink

# With sensor-id for multiple cameras
gst-launch-1.0 nvarguscamerasrc sensor-id=0 ! 'video/x-raw(memory:NVMM),width=1920,height=1080,framerate=30/1' ! nvvidconv ! xvimagesink
```

**USB Camera Preview:**
```bash
gst-launch-1.0 v4l2src device=/dev/video1 ! 'video/x-raw,width=1920,height=1080,framerate=30/1' ! nvvidconv ! xvimagesink
```

### Hardware-Accelerated Encoding

**H.264 Encoding (CSI):**
```bash
gst-launch-1.0 nvarguscamerasrc ! \
    'video/x-raw(memory:NVMM),width=1920,height=1080,framerate=30/1' ! \
    nvv4l2h264enc bitrate=8000000 ! \
    h264parse ! \
    qtmux ! \
    filesink location=output.mp4
```

**H.265/HEVC Encoding:**
```bash
gst-launch-1.0 nvarguscamerasrc ! \
    'video/x-raw(memory:NVMM),width=3840,height=2160,framerate=30/1' ! \
    nvv4l2h265enc bitrate=20000000 ! \
    h265parse ! \
    qtmux ! \
    filesink location=output_4k.mp4
```

**VP9 Encoding:**
```bash
gst-launch-1.0 nvarguscamerasrc ! \
    'video/x-raw(memory:NVMM),width=1920,height=1080,framerate=30/1' ! \
    nvv4l2vp9enc bitrate=8000000 ! \
    matroskamux ! \
    filesink location=output.webm
```

### Streaming Pipelines

**RTSP Server:**
```bash
# Install gst-rtsp-server
gst-launch-1.0 nvarguscamerasrc ! \
    'video/x-raw(memory:NVMM),width=1920,height=1080,framerate=30/1' ! \
    nvv4l2h264enc bitrate=8000000 ! \
    h264parse ! \
    rtph264pay name=pay0 pt=96 ! \
    udpsink host=192.168.1.100 port=5000
```

**RTSP Client (receive):**
```bash
gst-launch-1.0 rtspsrc location=rtsp://192.168.1.10:8554/test ! \
    rtph264depay ! \
    h264parse ! \
    nvv4l2decoder ! \
    nvvidconv ! \
    xvimagesink
```

**UDP Streaming:**
```bash
# Sender
gst-launch-1.0 nvarguscamerasrc ! \
    'video/x-raw(memory:NVMM),width=1920,height=1080,framerate=30/1' ! \
    nvv4l2h264enc ! \
    h264parse ! \
    rtph264pay ! \
    udpsink host=192.168.1.100 port=5000

# Receiver
gst-launch-1.0 udpsrc port=5000 caps='application/x-rtp,encoding-name=H264' ! \
    rtph264depay ! \
    h264parse ! \
    nvv4l2decoder ! \
    nvvidconv ! \
    xvimagesink
```

### Advanced Processing

**On-the-fly Rotation:**
```bash
gst-launch-1.0 nvarguscamerasrc ! \
    'video/x-raw(memory:NVMM),width=1920,height=1080,framerate=30/1' ! \
    nvvidconv flip-method=2 ! \  # 0=none, 1=ccw-90, 2=rot-180, 3=cw-90
    xvimagesink
```

**Crop and Scale:**
```bash
gst-launch-1.0 nvarguscamerasrc ! \
    'video/x-raw(memory:NVMM),width=3840,height=2160' ! \
    nvvidconv left=100 right=100 top=100 bottom=100 ! \
    'video/x-raw(memory:NVMM),width=1920,height=1080' ! \
    xvimagesink
```

**Overlay (Picture-in-Picture):**
```bash
gst-launch-1.0 nvcompositor name=comp \
    sink_0::xpos=0 sink_0::ypos=0 sink_0::width=1920 sink_0::height=1080 \
    sink_1::xpos=1440 sink_1::ypos=810 sink_1::width=480 sink_1::height=270 ! \
    nvvidconv ! xvimagesink \
    nvarguscamerasrc sensor-id=0 ! 'video/x-raw(memory:NVMM),width=1920,height=1080' ! comp.sink_0 \
    nvarguscamerasrc sensor-id=1 ! 'video/x-raw(memory:NVMM),width=480,height=270' ! comp.sink_1
```

### Python GStreamer

```python
#!/usr/bin/env python3
import gi
gi.require_version('Gst', '1.0')
from gi.repository import Gst, GLib
import sys

def main():
    Gst.init(None)

    # Create pipeline
    pipeline_str = (
        "nvarguscamerasrc ! "
        "video/x-raw(memory:NVMM),width=1920,height=1080,framerate=30/1 ! "
        "nvvidconv ! "
        "video/x-raw,format=BGRx ! "
        "videoconvert ! "
        "video/x-raw,format=BGR ! "
        "appsink name=sink emit-signals=true sync=false"
    )

    pipeline = Gst.parse_launch(pipeline_str)
    appsink = pipeline.get_by_name('sink')

    # Callback for new frames
    def on_new_sample(sink):
        sample = sink.emit('pull-sample')
        if sample:
            buffer = sample.get_buffer()
            caps = sample.get_caps()

            # Get frame data
            success, map_info = buffer.map(Gst.MapFlags.READ)
            if success:
                # Process frame (map_info.data contains frame bytes)
                print(f"Frame size: {len(map_info.data)} bytes")
                buffer.unmap(map_info)

        return Gst.FlowReturn.OK

    appsink.connect('new-sample', on_new_sample)

    # Start pipeline
    pipeline.set_state(Gst.State.PLAYING)

    # Run
    try:
        loop = GLib.MainLoop()
        loop.run()
    except KeyboardInterrupt:
        pass

    # Cleanup
    pipeline.set_state(Gst.State.NULL)

if __name__ == '__main__':
    main()
```

## ISP (Image Signal Processor) Tuning

### Argus Camera API

**Camera Override File:**
```bash
# Location: /var/nvidia/nvcam/settings/
# File: camera_overrides.isp

# Example overrides
property.gain.override=1
property.gain.value=2.0

property.exposure.override=1
property.exposure.value=30000

property.whitebalance.override=1
property.whitebalance.mode=manual
property.whitebalance.gains="1.5 1.0 1.0 1.5"
```

### NVCameraTools

```bash
# Install tools (if available)
apt-get install nvidia-l4t-camera

# Capture RAW image
nvgstcapture-1.0 --camsrc=0 --cap-dev-node=0 --mode=2

# Access camera properties
nvarguscamerasrc wbmode=0  # 0=off, 1=auto, 9=manual
nvarguscamerasrc aelock=true
nvarguscamerasrc awblock=true
nvarguscamerasrc exposuretimerange="30000 30000"  # Fixed exposure (30ms)
nvarguscamerasrc gainrange="1 1"  # Fixed gain
```

### Custom ISP Configuration

**Device Tree ISP Settings:**
```dts
/ {
    tegra-camera-platform {
        compatible = "nvidia, tegra-camera-platform";

        modules {
            module0 {
                badge = "imx219_bottom";
                position = "bottom";
                orientation = "0";

                drivernode0 {
                    pcl_id = "v4l2_sensor";

                    mode0 {
                        num_lanes = "2";
                        tegra_sinterface = "serial_a";
                        discontinuous_clk = "yes";
                        dpcm_enable = "false";
                        cil_settletime = "0";

                        active_w = "3280";
                        active_h = "2464";
                        pixel_t = "bayer_rggb";
                        readout_orientation = "90";
                        line_length = "3448";
                        inherent_gain = "1";
                        mclk_multiplier = "25";
                        pix_clk_hz = "182400000";

                        gain_factor = "16";
                        framerate_factor = "1000000";
                        exposure_factor = "1000000";
                        min_gain_val = "16";
                        max_gain_val = "170";
                        step_gain_val = "1";
                        min_exp_time = "13";
                        max_exp_time = "683709";
                        step_exp_time = "1";
                    };
                };
            };
        };
    };
};
```

### Auto Exposure/White Balance Algorithm

**Custom AE/AWB (C++ example concept):**
```cpp
// Simplified concept - actual implementation requires Argus API
class CustomAE {
public:
    void adjustExposure(float currentBrightness, float targetBrightness) {
        float error = targetBrightness - currentBrightness;
        float Kp = 0.5;  // Proportional gain

        float exposureAdjustment = error * Kp;
        newExposure = currentExposure + exposureAdjustment;

        // Clamp to valid range
        newExposure = std::clamp(newExposure, minExposure, maxExposure);

        // Apply to camera
        camera->setExposureTime(newExposure);
    }
};

class CustomAWB {
public:
    void adjustWhiteBalance(const Image& image) {
        // Calculate average RGB values in gray-world assumption
        float avgR = calculateAverage(image.redChannel());
        float avgG = calculateAverage(image.greenChannel());
        float avgB = calculateAverage(image.blueChannel());

        // Compute gains to balance to gray
        float gainR = avgG / avgR;
        float gainB = avgG / avgB;

        // Apply gains
        camera->setWBGains(gainR, 1.0f, 1.0f, gainB);
    }
};
```

## Multi-Camera Configurations

### Hardware Setup

**Camera Multiplexer (MUX):**
- TI TCA9548A (I2C multiplexer) for addressing multiple cameras with same I2C address
- Deserializer chips for GMSL/FPD-Link cameras

**Synchronized Capture:**
- Frame synchronization via hardware trigger
- Requires custom carrier board or external sync circuit

### Multi-Camera Device Tree

```dts
/ {
    i2c@3180000 {
        tca9548@70 {
            compatible = "nxp,pca9548";
            reg = <0x70>;
            #address-cells = <1>;
            #size-cells = <0>;

            i2c@0 {
                reg = <0>;
                #address-cells = <1>;
                #size-cells = <0>;

                imx219_a@10 {
                    compatible = "sony,imx219";
                    reg = <0x10>;
                    sensor_model = "imx219";
                    /* ... */
                };
            };

            i2c@1 {
                reg = <1>;

                imx219_b@10 {
                    compatible = "sony,imx219";
                    reg = <0x10>;
                    sensor_model = "imx219";
                    /* ... */
                };
            };
        };
    };
};
```

### Multi-Camera GStreamer

**Dual Camera Side-by-Side:**
```bash
gst-launch-1.0 nvcompositor name=comp \
    sink_0::xpos=0 sink_0::ypos=0 sink_0::width=1920 sink_0::height=1080 \
    sink_1::xpos=1920 sink_1::ypos=0 sink_1::width=1920 sink_1::height=1080 ! \
    'video/x-raw(memory:NVMM),width=3840,height=1080' ! \
    nvvidconv ! \
    'video/x-raw(memory:NVMM),width=3840,height=1080' ! \
    nvv4l2h264enc ! \
    h264parse ! \
    qtmux ! \
    filesink location=stereo.mp4 \
    nvarguscamerasrc sensor-id=0 ! 'video/x-raw(memory:NVMM),width=1920,height=1080' ! comp.sink_0 \
    nvarguscamerasrc sensor-id=1 ! 'video/x-raw(memory:NVMM),width=1920,height=1080' ! comp.sink_1
```

**Quad Camera Grid:**
```bash
gst-launch-1.0 nvcompositor name=comp \
    sink_0::xpos=0 sink_0::ypos=0 sink_0::width=960 sink_0::height=540 \
    sink_1::xpos=960 sink_1::ypos=0 sink_1::width=960 sink_1::height=540 \
    sink_2::xpos=0 sink_2::ypos=540 sink_2::width=960 sink_2::height=540 \
    sink_3::xpos=960 sink_3::ypos=540 sink_3::width=960 sink_3::height=540 ! \
    nvvidconv ! xvimagesink \
    nvarguscamerasrc sensor-id=0 ! 'video/x-raw(memory:NVMM)' ! nvvidconv ! 'video/x-raw(memory:NVMM),width=960,height=540' ! comp.sink_0 \
    nvarguscamerasrc sensor-id=1 ! 'video/x-raw(memory:NVMM)' ! nvvidconv ! 'video/x-raw(memory:NVMM),width=960,height=540' ! comp.sink_1 \
    nvarguscamerasrc sensor-id=2 ! 'video/x-raw(memory:NVMM)' ! nvvidconv ! 'video/x-raw(memory:NVMM),width=960,height=540' ! comp.sink_2 \
    nvarguscamerasrc sensor-id=3 ! 'video/x-raw(memory:NVMM)' ! nvvidconv ! 'video/x-raw(memory:NVMM),width=960,height=540' ! comp.sink_3
```

### Synchronized Multi-Camera Python

```python
#!/usr/bin/env python3
import cv2
import threading

class MultiCamera:
    def __init__(self, num_cameras=2):
        self.cameras = []
        self.frames = {}
        self.locks = {}

        for i in range(num_cameras):
            pipeline = (
                f"nvarguscamerasrc sensor-id={i} ! "
                "video/x-raw(memory:NVMM),width=1920,height=1080,framerate=30/1 ! "
                "nvvidconv ! video/x-raw,format=BGRx ! "
                "videoconvert ! video/x-raw,format=BGR ! appsink"
            )
            cap = cv2.VideoCapture(pipeline, cv2.CAP_GSTREAMER)
            self.cameras.append(cap)
            self.frames[i] = None
            self.locks[i] = threading.Lock()

    def capture_thread(self, cam_id):
        while True:
            ret, frame = self.cameras[cam_id].read()
            if ret:
                with self.locks[cam_id]:
                    self.frames[cam_id] = frame

    def start(self):
        self.threads = []
        for i in range(len(self.cameras)):
            t = threading.Thread(target=self.capture_thread, args=(i,))
            t.daemon = True
            t.start()
            self.threads.append(t)

    def get_frames(self):
        frames = []
        for i in range(len(self.cameras)):
            with self.locks[i]:
                if self.frames[i] is not None:
                    frames.append(self.frames[i].copy())
                else:
                    frames.append(None)
        return frames

    def release(self):
        for cap in self.cameras:
            cap.release()

# Usage
multi_cam = MultiCamera(num_cameras=2)
multi_cam.start()

try:
    while True:
        frames = multi_cam.get_frames()

        if all(f is not None for f in frames):
            # Process synchronized frames
            combined = cv2.hconcat(frames)
            cv2.imshow('Multi-Camera', combined)

        if cv2.waitKey(1) & 0xFF == ord('q'):
            break
finally:
    multi_cam.release()
    cv2.destroyAllWindows()
```

## Testing Procedures

### Camera Detection Test

```bash
#!/bin/bash
echo "=== Camera Detection Test ==="

# Check V4L2 devices
echo "V4L2 Devices:"
v4l2-ctl --list-devices

# Check CSI cameras via Argus
echo -e "\nArgus CSI Cameras:"
v4l2-ctl --list-devices | grep -A 5 "vi-output"

# Test each camera
for cam in /dev/video*; do
    echo -e "\nTesting $cam:"
    v4l2-ctl -d $cam --all | grep -E "Card type|Driver version|Pixel Format"
done
```

### Frame Rate Test

```bash
# CSI camera FPS test
gst-launch-1.0 nvarguscamerasrc num-buffers=300 ! \
    'video/x-raw(memory:NVMM),width=1920,height=1080,framerate=30/1' ! \
    nvvidconv ! \
    fpsdisplaysink text-overlay=true video-sink=xvimagesink sync=false

# USB camera FPS test
gst-launch-1.0 v4l2src device=/dev/video1 num-buffers=300 ! \
    'video/x-raw,width=1920,height=1080,framerate=30/1' ! \
    fpsdisplaysink text-overlay=true video-sink=xvimagesink sync=false
```

### Latency Test

```python
#!/usr/bin/env python3
import cv2
import time
import numpy as np

pipeline = (
    "nvarguscamerasrc ! "
    "video/x-raw(memory:NVMM),width=1920,height=1080,framerate=30/1 ! "
    "nvvidconv ! video/x-raw,format=BGRx ! "
    "videoconvert ! appsink"
)

cap = cv2.VideoCapture(pipeline, cv2.CAP_GSTREAMER)

latencies = []
for i in range(100):
    start = time.time()
    ret, frame = cap.read()
    end = time.time()

    if ret:
        latency_ms = (end - start) * 1000
        latencies.append(latency_ms)
        print(f"Frame {i}: {latency_ms:.2f} ms")

print(f"\nAverage Latency: {np.mean(latencies):.2f} ms")
print(f"Min Latency: {np.min(latencies):.2f} ms")
print(f"Max Latency: {np.max(latencies):.2f} ms")

cap.release()
```

## Yocto Integration

### Camera Driver Recipe

```bitbake
# File: recipes-kernel/linux/linux-tegra_%.bbappend

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
    file://imx219-driver.patch \
    file://camera-device-tree.dts \
"

do_configure_prepend() {
    # Enable camera drivers
    echo 'CONFIG_VIDEO_IMX219=m' >> ${B}/.config
    echo 'CONFIG_VIDEO_OV5693=m' >> ${B}/.config
}
```

### GStreamer Packages

```bitbake
# File: recipes-core/images/tegra-camera-image.bb

require recipes-core/images/core-image-base.bb

IMAGE_INSTALL_append = " \
    gstreamer1.0 \
    gstreamer1.0-plugins-base \
    gstreamer1.0-plugins-good \
    gstreamer1.0-plugins-bad \
    gstreamer1.0-plugins-ugly \
    gstreamer1.0-libav \
    gstreamer1.0-plugins-tegra \
    nvidia-argus \
    nvidia-argus-daemon \
    nvgstcapture \
    nvgstplayer \
    v4l-utils \
"
```

## References

### Official Documentation
- [Jetson Linux Camera Guide](https://docs.nvidia.com/jetson/l4t-multimedia/group__LibargusAPI.html)
- [Argus Camera API](https://docs.nvidia.com/jetson/l4t-multimedia/group__LibargusAPI.html)
- [GStreamer Accelerated Plugins](https://docs.nvidia.com/jetson/l4t-multimedia/mmapi_overview.html)

### Camera Vendors
- [E-CON Systems](https://www.e-consystems.com/nvidia-jetson-cameras.asp)
- [Leopard Imaging](https://www.leopardimaging.com/nvidia-jetson-cameras/)
- [Arducam](https://www.arducam.com/nvidia-jetson-cameras/)

### Tools
- [V4L2 Documentation](https://www.kernel.org/doc/html/latest/userspace-api/media/v4l/v4l2.html)
- [GStreamer Documentation](https://gstreamer.freedesktop.org/documentation/)

---

**Document Version**: 1.0
**Last Updated**: 2025-11
**Maintained By**: Hardware Integration Agent
