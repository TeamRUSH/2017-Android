package net.teamrush27.vision2017.utils.calibration;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;

public abstract class FrameRender {
    protected CameraCalibrator mCalibrator;

    public abstract Mat render(CameraBridgeViewBase.CvCameraViewFrame inputFrame);
}