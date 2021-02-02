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
                "overlay=x=(main_w-overlay_w)/2:y=(main_h-overlay_h)/2", "-c:a", "copy",
                "-b:v", "5000k",
                newVideoFile.getPath()};

        return executeFFmpegCommand(videoSource, newVideoFile, complexCommand);
    }

    public static File mirrorVideoHorizontally(Context context, File videoSource) {
        String videoFileName = videoSource.getPath();
        String formatString = videoFileName.substring(videoFileName.lastIndexOf("."));
        String fileName = FileUtils.getRandomFileName(formatString);
        File newVideoFile = ArtemisFileUtils.Companion.newFile(context, fileName);

        String[] complexCommand = {
                "-i", videoFileName, "-vf", "hflip", "-c:a", "copy", "-b:v", "5000k", newVideoFile.getPath()
        };

        return executeFFmpegCommand(videoSource, newVideoFile, complexCommand);
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
                        ",setsar=1:1, fps=fps=24",
                "-b:v", "5000k",
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
