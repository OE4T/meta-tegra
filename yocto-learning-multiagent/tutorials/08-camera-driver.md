# Tutorial 08: Camera Driver Integration
## Connecting CSI and USB Cameras to Jetson

---

## Learning Objectives

After completing this tutorial, you will be able to:
- Understand MIPI CSI-2 camera interface
- Configure camera device tree entries
- Work with V4L2 (Video for Linux 2) framework
- Integrate USB cameras
- Use NVIDIA's camera stack (nvcamera, libargus)
- Capture and process camera streams
- Debug camera initialization issues

---

## Prerequisites Checklist

- [ ] Completed Tutorial 01-07 (through I2C sensors)
- [ ] CSI camera module (IMX219, IMX477, or compatible)
- [ ] Or USB webcam for testing
- [ ] Understanding of V4L2 basics
- [ ] GStreamer knowledge (basic)
- [ ] Jetson with camera connectors accessible

---

## Estimated Duration

**Total Time**: 6-8 hours
- Camera hardware setup: 1 hour
- Device tree configuration: 2 hours
- Driver integration: 2-3 hours
- GStreamer pipeline testing: 1-2 hours
- Advanced features: 1-2 hours

---

## Step-by-Step Instructions

### Step 1: Understand Jetson Camera Architecture

Jetson supports multiple camera interfaces:

```
Camera Options on Jetson Orin:
┌─────────────────────────────────────────┐
│  CSI Cameras (MIPI CSI-2)              │
│  - Up to 8 CSI lanes                    │
│  - Support for IMX219, IMX477, etc.    │
│  - High performance, low latency        │
├─────────────────────────────────────────┤
│  USB Cameras (UVC)                      │
│  - USB 3.0/3.1 support                  │
│  - Standard webcams                     │
│  - Plug and play                        │
├─────────────────────────────────────────┤
│  GMSL Cameras (Automotive)              │
│  - Long cable support (up to 15m)      │
│  - Multiple cameras per link            │
│  - Industrial/automotive use            │
└─────────────────────────────────────────┘
```

**Camera Pipeline**:
```
Sensor → CSI → VI (Video Input) → ISP → Encoder/Output
         ↓
    Device Tree
         ↓
    V4L2 Driver
         ↓
    libargus / GStreamer
```

### Step 2: USB Camera Quick Test

Start with a USB camera for immediate testing:

```bash
# On Jetson, connect USB webcam

# Check if detected
lsusb

# Expected output:
# Bus 001 Device 003: ID 046d:0825 Logitech, Inc. Webcam C270

# List video devices
ls /dev/video*
# /dev/video0  /dev/video1

# Check camera capabilities
v4l2-ctl --list-devices

# Expected:
# HD Webcam C270 (usb-3610000.xhci-2.1):
#         /dev/video0

# List supported formats
v4l2-ctl -d /dev/video0 --list-formats-ext

# Capture a test frame
v4l2-ctl -d /dev/video0 \
    --set-fmt-video=width=1280,height=720,pixelformat=MJPG \
    --stream-mmap \
    --stream-to=test.jpg \
    --stream-count=1

# View with GStreamer
gst-launch-1.0 v4l2src device=/dev/video0 ! \
    video/x-raw,width=1280,height=720 ! \
    videoconvert ! autovideosink
```

### Step 3: CSI Camera Hardware Connection

Connect a Raspberry Pi Camera Module V2 (IMX219):

```
Physical Connection:
┌──────────────────┐
│  Jetson Orin     │   Ribbon Cable
│                  │   ═══════════════╗
│  CAM0 Connector ╞═══════════════════╣ IMX219
│                  │                   ║ Camera
└──────────────────┘                   ╚═══════

Important:
1. Power off Jetson before connecting
2. Ribbon cable contacts face down on Jetson
3. Ribbon cable contacts face sensor on camera
4. Ensure connector is fully seated
5. Lock connector by pushing down latch
```

**Verify connection**:
```bash
# After powering on, check I2C
# IMX219 typically on address 0x10

i2cdetect -y -r 9  # CAM_I2C bus (check your board schematic)

# Expected (if IMX219 present):
#      0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f
# 00:          -- -- -- -- -- -- -- -- -- -- -- -- --
# 10: 10 -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
```

### Step 4: Create Device Tree for CSI Camera

Add IMX219 camera to device tree:

```bash
cd ~/yocto-jetson/meta-custom/recipes-kernel/dtb-overlays/files

cat > imx219-camera-overlay.dts << 'EOF'
/dts-v1/;
/plugin/;

#include <dt-bindings/gpio/tegra234-gpio.h>

/ {
    compatible = "nvidia,p3737-0000+p3701-0000", "nvidia,tegra234";

    fragment@0 {
        target-path = "/";
        __overlay__ {
            /* Camera board */
            camera_board_e3326 {
                compatible = "nvidia,camera-board";
                physical_w = "3.680";
                physical_h = "2.760";

                has_eeprom = <0>;

                /* Camera module */
                modules {
                    module0 {
                        status = "okay";
                        badge = "imx219_bottom_A6V26";
                        position = "bottom";
                        orientation = "1";

                        drivernode0 {
                            status = "okay";
                            pcl_id = "v4l2_sensor";

                            /* V4L2 subdev */
                            devname = "imx219 9-0010";
                            proc-device-tree = "/proc/device-tree/cam_i2cmux/i2c@0/imx219_a@10";
                        };
                    };
                };
            };
        };
    };

    fragment@1 {
        target = <&cam_i2c0>;  /* Camera I2C bus 0 */
        __overlay__ {
            status = "okay";

            imx219_a@10 {
                compatible = "sony,imx219";
                reg = <0x10>;

                /* Physical dimensions */
                physical_w = "3.680";
                physical_h = "2.760";

                /* Sensor model */
                sensor_model = "imx219";

                /* Default mode */
                mode0 {
                    mclk_khz = "24000";
                    num_lanes = "2";
                    tegra_sinterface = "serial_a";
                    phy_mode = "DPHY";
                    discontinuous_clk = "no";
                    dpcm_enable = "false";
                    cil_settletime = "0";

                    active_w = "3280";
                    active_h = "2464";
                    pixel_t = "bayer_rggb";
                    readout_orientation = "90";
                    line_length = "3448";
                    inherent_gain = "1";
                    mclk_multiplier = "9.33";
                    pix_clk_hz = "182400000";

                    min_gain_val = "1.0";
                    max_gain_val = "10.625";
                    step_gain_val = "0.125";
                    min_hdr_ratio = "1";
                    max_hdr_ratio = "1";
                    min_framerate = "1.462526";
                    max_framerate = "21.0";
                    step_framerate = "1";
                    min_exp_time = "13";
                    max_exp_time = "683709";
                    step_exp_time = "1";
                };

                /* Camera ports */
                ports {
                    #address-cells = <1>;
                    #size-cells = <0>;

                    port@0 {
                        reg = <0>;
                        imx219_out0: endpoint {
                            port-index = <0>;
                            bus-width = <2>;
                            remote-endpoint = <&csi_in0>;
                        };
                    };
                };
            };
        };
    };

    fragment@2 {
        target = <&csi_base>;
        __overlay__ {
            num-channels = <1>;

            channel@0 {
                reg = <0>;
                ports {
                    #address-cells = <1>;
                    #size-cells = <0>;
                    port@0 {
                        reg = <0>;
                        csi_in0: endpoint@0 {
                            port-index = <0>;
                            bus-width = <2>;
                            remote-endpoint = <&imx219_out0>;
                        };
                    };

                    port@1 {
                        reg = <1>;
                        csi_out0: endpoint@1 {
                            remote-endpoint = <&vi_in0>;
                        };
                    };
                };
            };
        };
    };

    fragment@3 {
        target = <&vi_base>;
        __overlay__ {
            num-channels = <1>;

            ports {
                #address-cells = <1>;
                #size-cells = <0>;
                port@0 {
                    reg = <0>;
                    vi_in0: endpoint {
                        port-index = <0>;
                        bus-width = <2>;
                        remote-endpoint = <&csi_out0>;
                    };
                };
            };
        };
    };
};
EOF
```

**Explanation**:
- `camera_board_e3326`: Camera board configuration
- `imx219_a@10`: I2C sensor definition
- `mode0`: Camera mode (resolution, frame rate, etc.)
- `ports`: Media controller port connections
- `csi_base/vi_base`: CSI and Video Input controllers

### Step 5: Create IMX219 Kernel Driver

The Linux kernel has an IMX219 driver, but we'll show key modifications:

```bash
# Most of the driver is in upstream kernel
# We just need to enable it in kernel config

cd ~/yocto-jetson/meta-custom/recipes-kernel/linux/linux-tegra

cat > camera-support.cfg << 'EOF'
# Media framework
CONFIG_MEDIA_SUPPORT=y
CONFIG_MEDIA_CAMERA_SUPPORT=y

# V4L2 core
CONFIG_VIDEO_V4L2=y
CONFIG_VIDEO_V4L2_SUBDEV_API=y
CONFIG_VIDEOBUF2_CORE=y
CONFIG_VIDEOBUF2_DMA_CONTIG=y

# Camera sensor drivers
CONFIG_VIDEO_IMX219=m
CONFIG_VIDEO_IMX477=m

# NVIDIA Tegra video drivers
CONFIG_VIDEO_TEGRA_VI=y
CONFIG_VIDEO_TEGRA_VI_CHAN=y

# Media controller
CONFIG_MEDIA_CONTROLLER=y
CONFIG_VIDEO_V4L2_SUBDEV_API=y
EOF

# Update kernel append
cat >> ~/yocto-jetson/meta-custom/recipes-kernel/linux/linux-tegra_%.bbappend << 'EOF'

SRC_URI:append = " file://camera-support.cfg"
EOF
```

### Step 6: Build Camera-Enabled Image

```bash
cd ~/yocto-jetson/builds/jetson-orin-agx

# Rebuild kernel with camera support
bitbake virtual/kernel -c clean
bitbake virtual/kernel

# Build overlay
bitbake imx219-camera-overlay

# Add to image
echo 'IMAGE_INSTALL:append = " \
    kernel-module-video-imx219 \
    imx219-camera-overlay \
    v4l-utils \
    gstreamer1.0-plugins-good \
    gstreamer1.0-plugins-bad \
"' >> conf/local.conf

# Rebuild image
bitbake core-image-minimal

# Flash to Jetson
```

### Step 7: Test CSI Camera

```bash
# On Jetson, after boot

# Load device tree overlay
cat /boot/overlays/imx219-camera-overlay.dtbo > \
    /sys/kernel/config/device-tree/overlays/imx219/dtbo

# Load camera driver
modprobe imx219

# Check video devices
v4l2-ctl --list-devices

# Expected:
# vi-output, imx219 9-0010 (platform:tegra-camrtc-ca):
#         /dev/video0

# Check camera capabilities
v4l2-ctl -d /dev/video0 --all

# List supported formats
v4l2-ctl -d /dev/video0 --list-formats-ext

# Capture test frame
v4l2-ctl -d /dev/video0 \
    --set-fmt-video=width=1920,height=1080,pixelformat=RG10 \
    --stream-mmap \
    --stream-to=capture.raw \
    --stream-count=1

# Simple GStreamer test
gst-launch-1.0 v4l2src device=/dev/video0 ! \
    'video/x-raw,width=1920,height=1080,framerate=30/1' ! \
    videoconvert ! autovideosink
```

### Step 8: Use NVIDIA Camera Stack (libargus)

For advanced features, use NVIDIA's Argus library:

```bash
# Create libargus test application
cat > ~/yocto-jetson/meta-custom/recipes-apps/camera-test/files/argus-camera-test.cpp << 'EOF'
/*
 * argus-camera-test.cpp - Simple camera capture using libargus
 */

#include <Argus/Argus.h>
#include <EGLStream/EGLStream.h>
#include <iostream>

using namespace Argus;

int main()
{
    /* Initialize Argus */
    UniqueObj<CameraProvider> cameraProvider(CameraProvider::create());

    ICameraProvider *iCameraProvider =
        interface_cast<ICameraProvider>(cameraProvider);

    if (!iCameraProvider) {
        std::cerr << "Failed to create CameraProvider" << std::endl;
        return 1;
    }

    /* Get camera devices */
    std::vector<CameraDevice*> cameraDevices;
    iCameraProvider->getCameraDevices(&cameraDevices);

    if (cameraDevices.empty()) {
        std::cerr << "No cameras available" << std::endl;
        return 1;
    }

    std::cout << "Found " << cameraDevices.size() << " camera(s)" << std::endl;

    /* Create capture session */
    UniqueObj<CaptureSession> captureSession(
        iCameraProvider->createCaptureSession(cameraDevices[0]));

    ICaptureSession *iCaptureSession =
        interface_cast<ICaptureSession>(captureSession);

    if (!iCaptureSession) {
        std::cerr << "Failed to create CaptureSession" << std::endl;
        return 1;
    }

    /* Create output stream */
    UniqueObj<OutputStreamSettings> streamSettings(
        iCaptureSession->createOutputStreamSettings(STREAM_TYPE_EGL));

    IOutputStreamSettings *iStreamSettings =
        interface_cast<IOutputStreamSettings>(streamSettings);

    iStreamSettings->setPixelFormat(PIXEL_FMT_YCbCr_420_888);
    iStreamSettings->setResolution(Size2D<uint32_t>(1920, 1080));

    UniqueObj<OutputStream> stream(
        iCaptureSession->createOutputStream(streamSettings.get()));

    /* Create capture request */
    UniqueObj<Request> request(
        iCaptureSession->createRequest());

    IRequest *iRequest = interface_cast<IRequest>(request);
    iRequest->enableOutputStream(stream.get());

    /* Submit repeating request for preview */
    uint32_t requestId = iCaptureSession->repeat(request.get());

    std::cout << "Camera preview running..." << std::endl;
    std::cout << "Press Enter to stop" << std::endl;
    std::cin.get();

    /* Stop */
    iCaptureSession->stopRepeat();
    iCaptureSession->waitForIdle();

    std::cout << "Capture session stopped" << std::endl;

    return 0;
}
EOF

# Create recipe
cat > ~/yocto-jetson/meta-custom/recipes-apps/camera-test/argus-camera-test_1.0.bb << 'EOF'
SUMMARY = "Simple camera test using libargus"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

DEPENDS = "libargus tegra-libraries"

SRC_URI = "file://argus-camera-test.cpp"

S = "${WORKDIR}"

do_compile() {
    ${CXX} ${CXXFLAGS} ${LDFLAGS} \
        -o argus-camera-test \
        argus-camera-test.cpp \
        -largus -lEGL -lGLESv2
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 argus-camera-test ${D}${bindir}/
}

RDEPENDS:${PN} = "libargus tegra-libraries"
EOF
```

### Step 9: GStreamer Camera Pipelines

Create useful GStreamer pipelines:

```bash
# Create pipeline scripts
cat > ~/capture-scripts/camera-preview.sh << 'EOF'
#!/bin/bash
# Simple camera preview using GStreamer

gst-launch-1.0 \
    nvarguscamerasrc sensor-id=0 ! \
    'video/x-raw(memory:NVMM),width=1920,height=1080,framerate=30/1' ! \
    nvvidconv ! \
    'video/x-raw,width=1280,height=720' ! \
    nvoverlaysink
EOF

cat > ~/capture-scripts/camera-encode.sh << 'EOF'
#!/bin/bash
# Capture H.264 encoded video

DURATION=${1:-10}  # Default 10 seconds

gst-launch-1.0 \
    nvarguscamerasrc sensor-id=0 num-buffers=$((30*DURATION)) ! \
    'video/x-raw(memory:NVMM),width=1920,height=1080,framerate=30/1,format=NV12' ! \
    nvv4l2h264enc bitrate=8000000 ! \
    h264parse ! \
    qtmux ! \
    filesink location=output.mp4

echo "Captured $DURATION seconds to output.mp4"
EOF

cat > ~/capture-scripts/camera-snapshot.sh << 'EOF'
#!/bin/bash
# Capture single JPEG snapshot

OUTPUT=${1:-snapshot.jpg}

gst-launch-1.0 \
    nvarguscamerasrc sensor-id=0 num-buffers=1 ! \
    'video/x-raw(memory:NVMM),width=3280,height=2464' ! \
    nvjpegenc ! \
    filesink location=$OUTPUT

echo "Snapshot saved to $OUTPUT"
EOF

chmod +x ~/capture-scripts/*.sh
```

### Step 10: Multi-Camera Configuration

Configure multiple cameras:

```bash
cat > ~/yocto-jetson/meta-custom/recipes-kernel/dtb-overlays/files/dual-camera-overlay.dts << 'EOF'
/dts-v1/;
/plugin/;

#include <dt-bindings/gpio/tegra234-gpio.h>

/ {
    compatible = "nvidia,p3737-0000+p3701-0000", "nvidia,tegra234";

    fragment@0 {
        target-path = "/";
        __overlay__ {
            camera_board_dual {
                compatible = "nvidia,camera-board";
                has_eeprom = <0>;

                modules {
                    /* Camera 0 */
                    module0 {
                        status = "okay";
                        badge = "imx219_cam0";
                        position = "rear";
                        orientation = "1";

                        drivernode0 {
                            status = "okay";
                            pcl_id = "v4l2_sensor";
                            devname = "imx219 9-0010";
                            proc-device-tree = "/proc/device-tree/cam_i2c0/imx219_a@10";
                        };
                    };

                    /* Camera 1 */
                    module1 {
                        status = "okay";
                        badge = "imx219_cam1";
                        position = "front";
                        orientation = "1";

                        drivernode0 {
                            status = "okay";
                            pcl_id = "v4l2_sensor";
                            devname = "imx219 10-0010";
                            proc-device-tree = "/proc/device-tree/cam_i2c1/imx219_b@10";
                        };
                    };
                };
            };
        };
    };

    /* Camera 0 on CAM0 connector */
    fragment@1 {
        target = <&cam_i2c0>;
        __overlay__ {
            status = "okay";

            imx219_a@10 {
                compatible = "sony,imx219";
                reg = <0x10>;
                sensor_model = "imx219";
                /* ... mode configuration ... */
            };
        };
    };

    /* Camera 1 on CAM1 connector */
    fragment@2 {
        target = <&cam_i2c1>;
        __overlay__ {
            status = "okay";

            imx219_b@10 {
                compatible = "sony,imx219";
                reg = <0x10>;
                sensor_model = "imx219";
                /* ... mode configuration ... */
            };
        };
    };
};
EOF

# Test dual camera
gst-launch-1.0 \
    nvarguscamerasrc sensor-id=0 ! \
    'video/x-raw(memory:NVMM),width=1920,height=1080,framerate=30/1' ! \
    nvvidconv ! videoscale ! video/x-raw,width=640,height=480 ! comp.sink_0 \
    nvarguscamerasrc sensor-id=1 ! \
    'video/x-raw(memory:NVMM),width=1920,height=1080,framerate=30/1' ! \
    nvvidconv ! videoscale ! video/x-raw,width=640,height=480 ! comp.sink_1 \
    compositor name=comp sink_0::xpos=0 sink_1::xpos=640 ! \
    nvoverlaysink
```

---

## Troubleshooting Common Issues

### Issue 1: Camera Not Detected

**Symptoms**: No /dev/video device appears

**Solutions**:
```bash
# Check device tree overlay loaded
ls /proc/device-tree/ | grep camera

# Check I2C communication
i2cdetect -y -r 9  # Replace 9 with correct bus

# Check kernel driver loaded
lsmod | grep imx219

# Load driver manually
modprobe imx219

# Check kernel messages
dmesg | grep -i camera
dmesg | grep -i imx219
dmesg | grep -i csi

# Common errors to look for:
# - "Probe failed" - driver couldn't initialize
# - "I2C transfer error" - communication problem
# - "No device found" - wrong I2C address
```

### Issue 2: Black or Corrupted Image

**Symptoms**: Camera initializes but image is black/corrupted

**Solutions**:
```bash
# Check MCLK (master clock) is running
# MCLK should be 24MHz for most sensors
cat /sys/kernel/debug/clk/clk_summary | grep cam

# Verify sensor receives power
# Check device tree power supply definitions

# Test with known-good pipeline
gst-launch-1.0 nvarguscamerasrc sensor-id=0 ! \
    nvoverlaysink

# Enable sensor debugging
echo "file drivers/media/i2c/imx219.c +p" > /sys/kernel/debug/dynamic_debug/control

# Check exposure settings
v4l2-ctl -d /dev/video0 --get-ctrl=exposure
v4l2-ctl -d /dev/video0 --set-ctrl=exposure=1000
```

### Issue 3: Frame Rate Issues

**Symptoms**: Low frame rate or frame drops

**Solutions**:
```bash
# Check ISP frequency
cat /sys/kernel/debug/bpmp/debug/clk/isp/rate

# Monitor CSI errors
cat /sys/kernel/debug/tegra_csi/csi*/status

# Reduce resolution for testing
gst-launch-1.0 nvarguscamerasrc ! \
    'video/x-raw(memory:NVMM),width=1280,height=720,framerate=60/1' ! \
    nvoverlaysink

# Check buffer allocation
dmesg | grep -i "buffer\|memory"
```

---

## Verification Checklist

- [ ] USB camera detected and working
- [ ] CSI camera hardware connected correctly
- [ ] I2C communication with sensor confirmed
- [ ] Device tree overlay loads without errors
- [ ] Camera driver module loads successfully
- [ ] /dev/video0 device appears
- [ ] v4l2-ctl can query camera
- [ ] Can capture test frame
- [ ] GStreamer preview works
- [ ] H.264 encoding functional
- [ ] JPEG snapshot works
- [ ] Frame rate matches expectations
- [ ] No artifacts or corruption in image

---

## Next Steps

### Immediate Practice
1. Implement auto-exposure control
2. Add white balance adjustment
3. Create timelapse capture script

### Proceed to Next Tutorial
**Tutorial 09: AI Inference Pipeline** - Use cameras with AI

### Advanced Topics
- Custom ISP tuning
- HDR capture modes
- Synchronization of multiple cameras
- Low-light performance optimization

---

**Congratulations!** You can now integrate cameras with Jetson, configure device trees, use V4L2 and libargus, and create camera pipelines with GStreamer.

---

*Tutorial created by the Yocto & Meta-Tegra Multi-Agent Learning System*
*Last updated: 2025-01-15*
