package com.chemicalwedding.artemis.utils;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;

import java.io.File;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

public class VideoUtils {

    private static final String TAG = VideoUtils.class.getSimpleName();

    public static File watermarkVideo(Context context, File videoSource, File imageSource) {

        String videoFileName = videoSource.getPath();
        String formatString = videoFileName.substring(videoFileName.lastIndexOf("."));
        String fileName = FileUtils.getRandomFileName(formatString);
        File newVideoFile = ArtemisFileUtils.Companion.newFile(context, fileName);


        String[] complexCommand = {"-i", videoFileName, "-i", imageSource.getPath(), "-filter_complex",
                "overlay=x=(main_w-overlay_w)/2:y=(main_h-overlay_h)/2", "-c:a", "copy", newVideoFile.getPath()};

        return executeFFmpegCommand(videoSource, newVideoFile, complexCommand);
    }

    public static Rect calculateCropRect(RectF greenBox, RectF lens, RectF video) {
        float heightToWidthPercentage = greenBox.height() / greenBox.width();
        float newHeight = video.width() * heightToWidthPercentage;
        float croppedHeight = video.height() - newHeight;
        float halfCroppedheigth = croppedHeight / 2;

        RectF newVideo = new RectF(video.left, video.top + halfCroppedheigth, video.right, video.bottom - halfCroppedheigth);

        float topPercentage = lens.top  / greenBox.height();
        float bottomPercentage = lens.bottom / greenBox.height();
        float leftPercentage = lens.left / greenBox.width();
        float rightPercentage = lens.right / greenBox.width();

        Rect result =  new Rect((int) (newVideo.width() * leftPercentage),
                (int) (newVideo.height() * topPercentage),
                (int) (newVideo.width() * rightPercentage),
                (int) (newVideo.height() * bottomPercentage)
        );

        return result;
    }

    public static File cropVideo(File source, File dest, Rect cropRect) {
        String[] cmd = { "-i", source.getPath(), "-filter:v",
                "crop=" + cropRect.width() + ":" + cropRect.height() +
                        ":" + cropRect.left + ":" + cropRect.top,
                "-c:a", "copy",  // audio stream copied to avoid re-encoding
                dest.getPath()
        };

        return executeFFmpegCommand(source, dest, cmd);
    }

    public static File cropVideo(File source, int videoWidth, int videoHeight,
                                 int screenWidth, int screenHeight,
                                 RectF selectedLensBox,
                                 int cameraOverlayWidth, int cameraOverlayHeight) {
        String videoFileName = source.getPath();
        String videoFileNameCropped = videoFileName.substring(0, videoFileName.lastIndexOf("."));
        String formatString = videoFileName.substring(videoFileName.lastIndexOf("."));


        videoFileNameCropped = videoFileNameCropped + "_preview" + formatString;

        final float screenWRatio = (float) screenWidth / screenHeight;
        final float screenHRatio = (float) 1 / screenWRatio;

        int newVideoHeight = (int) (videoWidth * screenHRatio);
        int newVideoWidth = videoWidth;
        int newVideoWidthDiff = (int) ((videoWidth - newVideoWidth) / 2f);
        int newVideoHeightDiff = (int) ((videoHeight - newVideoHeight) / 2f);


        String cropVideoInputToScreen = newVideoWidth + ":" + newVideoHeight + ":" + newVideoWidthDiff + ":" + newVideoHeightDiff;
        String scaleScreenSize = cameraOverlayWidth + ":" + cameraOverlayHeight;
        String cropVideoToSelectedBox = selectedLensBox.width() + ":" + selectedLensBox.height() + ":" + selectedLensBox.left + ":" + selectedLensBox.top;

        String[] cmd = {"-y", "-i", videoFileName,
                "-filter:v",
                "crop=" + cropVideoInputToScreen +
                        ",scale=" + scaleScreenSize +
                        ",crop=" + cropVideoToSelectedBox +
                        ",setsar=1:1, fps=fps=60",
                "-c:a", "copy", videoFileNameCropped};

        return executeFFmpegCommand(source, new File(videoFileNameCropped), cmd);
    }

    private static File executeFFmpegCommand(File source, File output, String[] ffmpegCommand) {
        int returnCode = FFmpeg.execute(ffmpegCommand);

        if(returnCode == RETURN_CODE_SUCCESS) {
            Log.i(TAG, "Command execution completed successfully");
            if(source.delete()) {
                return output;
            }
        } else if (returnCode == RETURN_CODE_CANCEL){
            Log.i(TAG, "Command execution cancelled by user.");
        } else {
            String sourceString = String.format(
                    "Command execution failed with rc=%d and the output below.",
                    returnCode
            );
            Log.i(Config.TAG, sourceString);
        }
        return source;
    }
}
