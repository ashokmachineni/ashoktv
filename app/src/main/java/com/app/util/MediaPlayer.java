package com.app.util;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;

import com.app.ashokui.MyApplication;
import com.app.ashokui.R;
import com.app.ashokui.TVPlayActivity;
import com.app.ashokui.YtPlayActivity;
import com.app.item.ItemChannel;

public class MediaPlayer {

  private Context mContext;
  private ItemChannel objBean;

  public MediaPlayer(Context context) {
    mContext = context;
  }

  public void playVideo(ItemChannel item) {
    objBean = item;

    if (objBean.isTv()) {
      if (MyApplication.getInstance().getExternalPlayer()) {
        showExternalPlay();
      } else {
        Intent intent = new Intent(mContext, TVPlayActivity.class);
        intent.putExtra("videoUrl", objBean.getChannelUrl());
        mContext.startActivity(intent);
      }
    } else {
      String videoId = NetworkUtils.getVideoId(objBean.getChannelUrl());
      Intent intent = new Intent(mContext, YtPlayActivity.class);
      intent.putExtra("id", videoId);
      mContext.startActivity(intent);
    }
  }

  private void showExternalPlay() {
    final Dialog mDialog = new Dialog(mContext, R.style.Theme_AppCompat_Translucent);
    mDialog.setContentView(R.layout.player_dialog);
    LinearLayout lytMxPlayer = mDialog.findViewById(R.id.lytMxPlayer);
    LinearLayout lytVLCPlayer = mDialog.findViewById(R.id.lytVLCPlayer);
    LinearLayout lytXMTVPlayer = mDialog.findViewById(R.id.lytXMTVPlayer);

    lytMxPlayer.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mDialog.dismiss();
        if (isExternalPlayerAvailable(mContext, "com.mxtech.videoplayer.ad")) {
          playMxPlayer();
        } else {
          appNotInstalledDownload(mContext.getString(R.string.mx_player), "com.mxtech.videoplayer.ad", false);
        }
      }
    });

    lytVLCPlayer.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mDialog.dismiss();
        if (isExternalPlayerAvailable(mContext, "org.videolan.vlc")) {
          playVlcPlayer();
        } else {
          appNotInstalledDownload(mContext.getString(R.string.vlc_player), "org.videolan.vlc", false);
        }
      }
    });

    lytXMTVPlayer.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mDialog.dismiss();
        if (isExternalPlayerAvailable(mContext, "com.xmtex.videoplayer.ads")) {
          playXmtvPlayer();
        } else {
          appNotInstalledDownload(mContext.getString(R.string.xmtv_player), "com.xmtex.videoplayer.ads", true);
        }
      }
    });


    mDialog.show();
  }

  private void playMxPlayer() {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    Uri videoUri = Uri.parse(objBean.getChannelUrl());
    intent.setDataAndType(videoUri, "application/x-mpegURL");
    intent.setPackage("com.mxtech.videoplayer.ad");
    mContext.startActivity(intent);
  }

  private void playVlcPlayer() {
    Uri uri = Uri.parse(objBean.getChannelUrl());
    Intent vlcIntent = new Intent(Intent.ACTION_VIEW);
    vlcIntent.setPackage("org.videolan.vlc");
    vlcIntent.setDataAndTypeAndNormalize(uri, "video/*");
    vlcIntent.setComponent(new ComponentName("org.videolan.vlc", "org.videolan.vlc.gui.video.VideoPlayerActivity"));
    mContext.startActivity(vlcIntent);
  }

  private void playXmtvPlayer() {
    Bundle bnd = new Bundle();
    bnd.putString("path", objBean.getChannelUrl());
    Intent intent = new Intent();
    intent.setClassName("com.xmtex.videoplayer.ads", "org.zeipel.videoplayer.XMTVPlayer");
    intent.putExtras(bnd);
    mContext.startActivity(intent);
  }

  private boolean isExternalPlayerAvailable(Context context, String packageName) {
    PackageManager pm = context.getPackageManager();
    boolean app_installed;
    try {
      pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
      app_installed = true;
    } catch (PackageManager.NameNotFoundException e) {
      app_installed = false;
    }
    return app_installed;
  }

  private void appNotInstalledDownload(final String appName, final String packageName, final boolean isDownloadExternal) {
    String text = mContext.getString(R.string.download_msg, appName);
    new AlertDialog.Builder(mContext)
            .setTitle(mContext.getString(R.string.important))
            .setMessage(text)
            .setPositiveButton(mContext.getString(R.string.download), new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
                if (isDownloadExternal) {
                  mContext.startActivity(new Intent(
                          Intent.ACTION_VIEW,
                          Uri.parse(mContext.getString(R.string.xmtv_download_link))));
                } else {
                  try {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id="
                                    + packageName)));
                  } catch (android.content.ActivityNotFoundException anfe) {
                    mContext.startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id="
                                    + packageName)));
                  }
                }
              }
            })
            .setNegativeButton(mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
                // do nothing
              }
            })
            .show();
  }
}
