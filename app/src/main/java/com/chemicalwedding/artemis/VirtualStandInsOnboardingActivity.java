package com.chemicalwedding.artemis;

import android.os.Bundle;

import com.codemybrainsout.onboarder.AhoyOnboarderActivity;
import com.codemybrainsout.onboarder.AhoyOnboarderCard;

import java.util.ArrayList;
import java.util.List;

public class VirtualStandInsOnboardingActivity extends AhoyOnboarderActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AhoyOnboarderCard ahoyOnboarderCard1 = new AhoyOnboarderCard(
                "Virtual stand-ins",
                "Virtual stand-ins are now real 3 dimensional models of people and vehicles.",
                R.drawable.virtualstandin);
        ahoyOnboarderCard1.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard1.setTitleColor(R.color.white);
        ahoyOnboarderCard1.setDescriptionColor(R.color.grey_200);
        ahoyOnboarderCard1.setTitleTextSize(dpToPixels(8, this));
        ahoyOnboarderCard1.setDescriptionTextSize(dpToPixels(6, this));

        AhoyOnboarderCard ahoyOnboarderCard2 = new AhoyOnboarderCard(
                "Gestures",
                "Use pinch and zoom to scale, two fingers to rotate and single finger to position in the frame",
                R.drawable.fingers);
        ahoyOnboarderCard2.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard2.setTitleColor(R.color.white);
        ahoyOnboarderCard2.setDescriptionColor(R.color.grey_200);
        ahoyOnboarderCard2.setTitleTextSize(dpToPixels(8, this));
        ahoyOnboarderCard2.setDescriptionTextSize(dpToPixels(6, this));

        AhoyOnboarderCard ahoyOnboarderCard3 = new AhoyOnboarderCard(
                "Color",
                "Color is customizable",
                R.drawable.silhouettes);
        ahoyOnboarderCard3.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard3.setTitleColor(R.color.white);
        ahoyOnboarderCard3.setDescriptionColor(R.color.grey_200);
        ahoyOnboarderCard3.setTitleTextSize(dpToPixels(8, this));
        ahoyOnboarderCard3.setDescriptionTextSize(dpToPixels(6, this));

        AhoyOnboarderCard ahoyOnboarderCard4 = new AhoyOnboarderCard(
                "Adding models",
                "Click + to add more than one stand-in to create scenes.",
                R.drawable.add);
        ahoyOnboarderCard4.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard4.setTitleColor(R.color.white);
        ahoyOnboarderCard4.setDescriptionColor(R.color.grey_200);
        ahoyOnboarderCard4.setTitleTextSize(dpToPixels(8, this));
        ahoyOnboarderCard4.setDescriptionTextSize(dpToPixels(6, this));

        AhoyOnboarderCard ahoyOnboarderCard5 = new AhoyOnboarderCard(
                "Editing",
                "You can press and hold the model at any time to edit it.",
                R.drawable.edit);
        ahoyOnboarderCard5.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard5.setTitleColor(R.color.white);
        ahoyOnboarderCard5.setDescriptionColor(R.color.grey_200);
        ahoyOnboarderCard5.setTitleTextSize(dpToPixels(8, this));
        ahoyOnboarderCard5.setDescriptionTextSize(dpToPixels(6, this));

        List<AhoyOnboarderCard> pages = new ArrayList<>();
        pages.add(ahoyOnboarderCard1);
        pages.add(ahoyOnboarderCard2);
        pages.add(ahoyOnboarderCard3);
        pages.add(ahoyOnboarderCard4);
        pages.add(ahoyOnboarderCard5);

        List<Integer> colorList = new ArrayList<>();
        setOnboardPages(pages);
        colorList.add(R.color.coral);
        colorList.add(R.color.dark_spring_green);
        colorList.add(R.color.cerulean);
        colorList.add(R.color.ash_gray);
        colorList.add(R.color.chili_red);
        setColorBackground(colorList);
        setFinishButtonTitle("Ok");
        showNavigationControls(true);
    }

    @Override
    public void onFinishButtonPressed() {
        finish();
    }
}
