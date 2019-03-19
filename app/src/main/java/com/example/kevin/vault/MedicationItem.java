package com.example.kevin.vault;

import android.widget.Spinner;

public class MedicationItem {

    private int mImageResource;
    private String mText1; //Medication Name
    private String mText2; //Medication Dosage
    private String mText3; //Medication Reason
    private String mText4; //Medication Instructions
    private String mText5; //Medication Type
    private String mText6; //Start Date
    private String mText7; //Period
    private String coreText1;
    private String coreText2;
    private String coreText3;
    private String coreText4;
    private String coreText5;
    private String coreText6;
    private String coreText7;

    public MedicationItem(int imageResource, String text1, String text2, String text3, String text4, String text5, String text6, String text7) {
        mImageResource = imageResource;
        mText1 = text1;
        mText2 = text2;
        mText3 = text3;
        mText4 = text4;
        mText5 = text5;
        mText6 = text6;
        mText7 = text7;

        coreText1 = text1;
        coreText2 = text2;
        coreText3 = text3;
        coreText4 = text4;
        coreText5 = text5;
        coreText6 = text6;
        coreText7 = text7;
    }

    public void resetText() {
        mText1 = coreText1;
        mText2 = coreText2;
        mText3 = coreText3;
        mText4 = coreText4;
        mText5 = coreText5;
        mText6 = coreText6;
        mText7 = coreText7;
    }


    public int getImageResource() {
        return mImageResource;
    }

    public String getText5() { return mText5; }

    public String getText1() {
        return mText1;
    }

    public String getText2() {
        return mText2;
    }

    public String getText3() {
        return mText3;
    }

    public String getText4() {
        return mText4;
    }

    public String getText6() {
        return mText6;
    }

    public String getText7() {
        return mText7;
    }

    public void setText1(String text1) { mText1 = text1; }
    public void setText2(String text2) { mText2 = text2; }
    public void setText3(String text3) { mText3 = text3; }
    public void setText4(String text4) { mText4 = text4; }
    public void setText5(String text5) { mText5 = text5; }
    public void setText6(String text6) { mText6 = text6; }
    public void setText7(String text7) { mText7 = text7; }
    public void setImage(int imageres) { mImageResource = imageres; }

}
