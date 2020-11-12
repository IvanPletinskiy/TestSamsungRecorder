package com.sec.android.app.voicenote.p007ui;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import androidx.annotation.Nullable;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.service.SimpleEngine;
import com.sec.android.app.voicenote.uicore.SimpleFragmentController;

/* renamed from: com.sec.android.app.voicenote.ui.SimpleSeekFragment */
public class SimpleSeekFragment extends AbsSimpleFragment implements SimpleEngine.OnSimpleEngineListener, SimpleFragmentController.OnSceneChangeListener, SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "SimpleSeekFragment";
    private int mDuration = 0;
    private Handler mEngineEventHandler = null;
    private int mScene = 0;
    private SeekBar mSeekBar;
    private View view;

    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Log.m26i(TAG, "onCreateView");
        this.view = layoutInflater.inflate(C0690R.layout.fragment_seek, viewGroup, false);
        this.mSeekBar = (SeekBar) this.view.findViewById(C0690R.C0693id.seekbar_simple);
        this.mSeekBar.setThumbTintList(colorToColorStateList(getResources().getColor(C0690R.C0691color.listrow_seekbar_fg_color, (Resources.Theme) null)));
        return this.view;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mEngineEventHandler = new Handler(new Handler.Callback() {
            public final boolean handleMessage(Message message) {
                return SimpleSeekFragment.this.lambda$onCreate$0$SimpleSeekFragment(message);
            }
        });
    }

    public /* synthetic */ boolean lambda$onCreate$0$SimpleSeekFragment(Message message) {
        SimpleEngine simpleEngine;
        if (getActivity() == null || !isAdded() || isRemoving() || (simpleEngine = this.mSimpleEngine) == null) {
            Log.m22e(TAG, "mEngineEventHandler RETURN by : " + getActivity() + ',' + isAdded() + ',' + isAdded());
            return false;
        }
        int i = message.what;
        if (i != 1010) {
            if (i != 1011) {
                switch (i) {
                    case 2010:
                        this.view.setVisibility(0);
                        this.mSeekBar.setVisibility(0);
                        this.mSeekBar.setMax(this.mSimpleEngine.getDuration());
                        this.mSeekBar.setProgress(this.mSimpleEngine.getCurrentTime());
                        this.mSeekBar.setOnSeekBarChangeListener(this);
                        int i2 = message.arg1;
                        if ((i2 == 1 || i2 == 2) && this.mSimpleEngine.getRecorderState() == 1 && this.mSimpleEngine.getPlayerState() != 3) {
                            this.mDuration = 0;
                            this.mSimpleEngine.setCurrentTime(this.mDuration);
                            this.mSimpleEngine.setCurrentTime(this.mDuration, true);
                            break;
                        }
                    case 2011:
                        this.mSeekBar.setProgress(0);
                        this.mSeekBar.setVisibility(0);
                        break;
                    case 2012:
                        int playerState = simpleEngine.getPlayerState();
                        if (playerState == 3 || playerState == 4 || playerState == 2) {
                            int i3 = message.arg1;
                            this.mDuration = i3;
                            this.mSeekBar.setProgress(i3);
                            this.mSimpleEngine.setCurrentTime(this.mDuration, true);
                            break;
                        }
                }
            } else {
                this.mDuration = message.arg1;
                if (simpleEngine.getRecorderState() != 1) {
                    this.mSimpleEngine.setCurrentTime(this.mDuration, true);
                }
            }
        }
        return false;
    }

    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        if (z) {
            this.mSimpleEngine.seekTo(seekBar.getProgress());
        }
    }

    public void onDestroy() {
        this.mEngineEventHandler = null;
        super.onDestroy();
    }

    public void onDestroyView() {
        this.mSimpleEngine.unregisterListener(this);
        super.onDestroyView();
    }

    public void onViewCreated(View view2, Bundle bundle) {
        Log.m26i(TAG, "onViewCreated - bundle : " + bundle);
        super.onViewCreated(view2, bundle);
        Handler handler = this.mEngineEventHandler;
        handler.sendMessage(handler.obtainMessage(2013, 0, 0));
        this.mSimpleEngine.registerListener(this);
    }

    public void onEngineUpdate(int i, int i2, int i3) {
        Log.m26i(TAG, "onEngineUpdate : " + i);
        Handler handler = this.mEngineEventHandler;
        if (handler != null) {
            handler.sendMessage(handler.obtainMessage(i, i2, i3));
        }
    }

    public void onUpdate(Object obj) {
        Log.m26i(TAG, "onUpdate : " + obj);
        Integer num = (Integer) obj;
        int intValue = num.intValue();
        if (intValue == 982) {
            this.mSimpleEngine.setCurrentTime(0);
        } else if (intValue == 1007) {
            this.mSeekBar.setVisibility(0);
        } else if (intValue == 50003) {
            this.mSeekBar.setVisibility(8);
        } else if (intValue == 50010) {
            this.mSimpleEngine.setCurrentTime(0);
        }
        this.mCurrentEvent = num.intValue();
    }

    public void onSceneChange(int i) {
        Log.m26i(TAG, "onSceneChange scene : " + i + " mScene : " + this.mScene);
        if (isAdded() && !isRemoving() && this.mScene != i) {
            this.mScene = i;
            this.mSimpleEngine.setScene(this.mScene);
            int i2 = this.mScene;
            if (i2 == 1) {
                this.mSeekBar.setVisibility(4);
            } else if (i2 != 3) {
                this.mSeekBar.setVisibility(4);
            } else {
                this.mSeekBar.setVisibility(0);
            }
        }
    }

    public ColorStateList colorToColorStateList(int i) {
        return new ColorStateList(new int[][]{new int[0]}, new int[]{i});
    }
}
