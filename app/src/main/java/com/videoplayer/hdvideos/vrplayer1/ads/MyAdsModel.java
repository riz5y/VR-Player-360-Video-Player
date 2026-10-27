package com.videoplayer.hdvideos.vrplayer1.ads;

public class MyAdsModel {
    private String appid_admob_inApp;
    private String banner_admob_inApp;
    private String banner_large_admob_inApp;
    private String app_open_high_splash_ad_id_admob;
    private String app_open_ad_id_admob;
    private String native_admob_inApp;
    private String interstitial_admob_inApp;
    private boolean should_show_admob;

    public double current_counter;
    public double next_ads_time;
    public boolean ctr_control;
    public int adShowAfter;

    public MyAdsModel() {
    }


    public MyAdsModel(String appid_admob_inApp, String banner_admob_inApp, String banner_large_admob_inApp, String app_open_high_splash_ad_id_admob, String app_open_ad_id_admob, String native_admob_inApp, String interstitial_admob_inApp,
                      boolean should_show_admob, double current_counter, double next_ads_time, boolean ctr_control, int adShowAfter) {
        this.appid_admob_inApp = appid_admob_inApp;
        this.banner_admob_inApp = banner_admob_inApp;
        this.banner_large_admob_inApp = banner_large_admob_inApp;
        this.app_open_high_splash_ad_id_admob = app_open_high_splash_ad_id_admob;
        this.app_open_ad_id_admob = app_open_ad_id_admob;
        this.native_admob_inApp = native_admob_inApp;
        this.interstitial_admob_inApp = interstitial_admob_inApp;
        this.should_show_admob = should_show_admob;
        this.current_counter = current_counter;
        this.next_ads_time = next_ads_time;
        this.ctr_control = ctr_control;
        this.adShowAfter = adShowAfter;
    }

    public int getAdShowAfter() {
        return adShowAfter;
    }

    public void setAdShowAfter(int adShowAfter) {
        this.adShowAfter = adShowAfter;
    }

    public String getAppid_admob_inApp() {
        return appid_admob_inApp;
    }

    public void setAppid_admob_inApp(String appid_admob_inApp) {
        this.appid_admob_inApp = appid_admob_inApp;
    }

    public String getBanner_admob_inApp() {
        return banner_admob_inApp;
    }

    public void setBanner_admob_inApp(String banner_admob_inApp) {
        this.banner_admob_inApp = banner_admob_inApp;
    }

    public String getBanner_large_admob_inApp() {
        return banner_large_admob_inApp;
    }

    public void setBanner_large_admob_inApp(String banner_large_admob_inApp) {
        this.banner_large_admob_inApp = banner_large_admob_inApp;
    }

    public String getApp_open_high_splash_ad_id_admob() {
        return app_open_high_splash_ad_id_admob;
    }

    public void setApp_open_high_splash_ad_id_admob(String app_open_high_splash_ad_id_admob) {
        this.app_open_high_splash_ad_id_admob = app_open_high_splash_ad_id_admob;
    }

    public String getApp_open_ad_id_admob() {
        return app_open_ad_id_admob;
    }

    public void setApp_open_ad_id_admob(String app_open_ad_id_admob) {
        this.app_open_ad_id_admob = app_open_ad_id_admob;
    }

    public String getInterstitial_admob_inApp() {
        return interstitial_admob_inApp;
    }

    public void setInterstitial_admob_inApp(String interstitial_admob_inApp) {
        this.interstitial_admob_inApp = interstitial_admob_inApp;
    }

    public boolean isShould_show_admob() {
        return should_show_admob;
    }

    public void setShould_show_admob(boolean should_show_admob) {
        this.should_show_admob = should_show_admob;
    }

    public String getNative_admob_inApp() {
        return native_admob_inApp;
    }

    public void setNative_admob_inApp(String native_admob_inApp) {
        this.native_admob_inApp = native_admob_inApp;
    }

    public double getCurrent_counter() {
        return current_counter;
    }

    public void setCurrent_counter(double current_counter) {
        this.current_counter = current_counter;
    }

    public double getNext_ads_time() {
        return next_ads_time;
    }

    public void setNext_ads_time(double next_ads_time) {
        this.next_ads_time = next_ads_time;
    }

    public boolean isCtr_control() {
        return ctr_control;
    }

    public void setCtr_control(boolean ctr_control) {
        this.ctr_control = ctr_control;
    }


    //    @Override
//    public String toString() {
//        return "MyAdsModel{" +
//                "appid_admob_inApp='" + appid_admob_inApp + '\'' +
//                ", banner_admob_inApp='" + banner_admob_inApp + '\'' +
//                ", banner_large_admob_inApp='" + banner_large_admob_inApp + '\'' +
//                ", interstitial_admob_inApp='" + interstitial_admob_inApp + '\'' +
//                ", should_show_admob=" + should_show_admob +
//                ", native_admob_inApp='" + native_admob_inApp + '\'' +
//                ", app_open_high_splash_ad_id_admob='" + app_open_high_splash_ad_id_admob + '\'' +
//                ", app_open_ad_id_admob='" + app_open_ad_id_admob + '\'' +
//                '}';
//    }
  }
