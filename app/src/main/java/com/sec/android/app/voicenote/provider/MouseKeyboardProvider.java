package com.sec.android.app.voicenote.provider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.ListView;

import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.DataRepository;
import com.sec.android.app.voicenote.p007ui.AbsFragment;
import com.sec.android.app.voicenote.p007ui.actionbar.RunOptionMenu;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;

import java.util.Iterator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class MouseKeyboardProvider {
    private static final String TAG = "MouseKeyboardProvider";
    private static MouseKeyboardProvider mInstance;
    private int currentScene = 0;
    private boolean mIsCtrlPressed = false;
    private boolean mIsShiftPressed = false;
    private boolean mSelectModeByEditOption = false;
    private boolean mShareSelectMode = false;
    private int mTouchPos = -1;
    /* access modifiers changed from: private */
    public long moveId = -1;
    /* access modifiers changed from: private */
    public int movePosition = -1;

    public static class PointerType {
        public static final int ARROW = 1;
        public static final int GRAB = 2;
        public static final int GRABBING = 3;
        public static final int WAIT = 4;
    }

    private MouseKeyboardProvider() {
        Log.m19d(TAG, "MouseKeyboardProvider creator !!");
    }

    public static MouseKeyboardProvider getInstance() {
        if (mInstance == null) {
            mInstance = new MouseKeyboardProvider();
        }
        return mInstance;
    }

    private void setCtrlPressed(boolean z) {
        this.mIsCtrlPressed = z;
    }

    public boolean isCtrlPressed() {
        return this.mIsCtrlPressed;
    }

    /* access modifiers changed from: private */
    public void setShiftPressed(boolean z) {
        this.mIsShiftPressed = z;
    }

    public boolean isShiftPressed() {
        return this.mIsShiftPressed;
    }

    public void setTouchPosition(int i) {
        this.mTouchPos = i;
    }

    public int getTouchPosition() {
        return this.mTouchPos;
    }

    public void setSelectModeByEditOption(boolean z) {
        this.mSelectModeByEditOption = z;
    }

    public boolean getSelectModeByEditOption() {
        return this.mSelectModeByEditOption;
    }

    public void setShareSelectMode(boolean z) {
        this.mShareSelectMode = z;
    }

    public boolean getShareSelectMode() {
        return this.mShareSelectMode;
    }

    public void keyEventInteraction(AppCompatActivity appCompatActivity, KeyEvent keyEvent, int i) {
        CheckBox checkBox;
        Log.m26i(TAG, "KeyEventInteraction");
        if (appCompatActivity != null) {
            ContextMenuProvider.getInstance().setId(-1);
            if (keyEvent != null) {
                int keyCode = keyEvent.getKeyCode();
                int action = keyEvent.getAction();
                Log.m26i(TAG, "onKeyEvent event.getKeyCode() : " + keyCode + " event.getAction()" + action);
                if (action != 0) {
                    if (action == 1) {
                        setCtrlPressed(false);
                        setShiftPressed(false);
                    }
                } else if (keyEvent.isCtrlPressed()) {
                    setCtrlPressed(true);
                } else if (keyEvent.isShiftPressed()) {
                    setShiftPressed(true);
                }
                int i2 = 3000;
                if (keyCode != 21) {
                    if (keyCode != 22) {
                        if (keyCode != 29) {
                            if (keyCode != 32 && keyCode != 34) {
                                if (keyCode != 67) {
                                    if (keyCode != 84) {
                                        if (keyCode != 112) {
                                            if (keyCode == 132) {
                                                RunOptionMenu.getInstance().showRenameDialog(appCompatActivity, i);
                                                return;
                                            } else if (keyCode != 122) {
                                                if (keyCode == 123 && i == 4) {
                                                    Engine.getInstance().setCurrentTime(Engine.getInstance().getDuration());
                                                    return;
                                                }
                                                return;
                                            } else if (i == 4) {
                                                Engine.getInstance().setCurrentTime(0);
                                                return;
                                            } else {
                                                return;
                                            }
                                        }
                                    }
                                }
                                if (i == 6) {
                                    VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.SHORTCUT_EDIT_TRIM_DIALOG));
                                    return;
                                } else if (keyCode != 67) {
                                    RunOptionMenu.getInstance().delete(i);
                                    return;
                                } else {
                                    return;
                                }
                            } else if (!isCtrlPressed()) {
                                return;
                            }
                            if (i != 7 && CursorProvider.getInstance().getItemCount() > 0) {
                                VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.START_SEARCH));
                                SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_SEARCH, -1);
                            }
                        } else if (isCtrlPressed()) {
                            if (Settings.getIntSettings(Settings.KEY_LIST_MODE, 0) != 1 || DataRepository.getInstance().getCategoryRepository().isChildList()) {
                                if ((i == 2 || i == 7) && CursorProvider.getInstance().getItemCount() > 0) {
                                    CheckedItemProvider.initCheckedList();
                                    Iterator<Long> it = CursorProvider.getInstance().getIDs().iterator();
                                    while (it.hasNext()) {
                                        CheckedItemProvider.toggle(it.next().longValue());
                                    }
                                    if (i != 7) {
                                        VoiceNoteObservable.getInstance().notifyObservers(6);
                                    } else {
                                        VoiceNoteObservable.getInstance().notifyObservers(13);
                                    }
                                } else if ((i == 5 || i == 10 || i == 9) && (checkBox = (CheckBox) appCompatActivity.getWindow().getDecorView().findViewById(C0690R.C0693id.optionbar_checkbox)) != null) {
                                    checkBox.performClick();
                                }
                            } else if (CursorProvider.getInstance().getCategoriesCount() > 0) {
                                VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.SHORTCUT_CTRLA_SELECT));
                            }
                        }
                    } else if (isShiftPressed() && i == 4) {
                        int currentTime = Engine.getInstance().getCurrentTime();
                        if (Engine.getInstance().getDuration() <= 15000) {
                            i2 = 1000;
                        }
                        int i3 = currentTime + i2;
                        if (i3 <= Engine.getInstance().getDuration()) {
                            Engine.getInstance().setCurrentTime(i3);
                        }
                    }
                } else if (isShiftPressed() && i == 4) {
                    int currentTime2 = Engine.getInstance().getCurrentTime();
                    if (Engine.getInstance().getDuration() <= 15000) {
                        i2 = 1000;
                    }
                    int i4 = currentTime2 - i2;
                    if (i4 >= 0) {
                        Engine.getInstance().setCurrentTime(i4);
                    }
                }
            }
        }
    }

    public void setCurrentScene(int i) {
        this.currentScene = i;
    }

    public int getCurrentScene() {
        return this.currentScene;
    }

    public void changePointerIcon(View view, Context context, int i) {
        if (view != null && context != null) {
            if (i == 1) {
                setPointerIcon(view, context, 1000);
            } else if (i == 2) {
                setPointerIcon(view, context, 1020);
            } else if (i == 3) {
                setPointerIcon(view, context, 1021);
            } else if (i == 4) {
                setPointerIcon(view, context, 1004);
            }
        }
    }

    @TargetApi(24)
    private void setPointerIcon(View view, Context context, int i) {
        if (view != null && context != null) {
//            view.semSetPointerIcon(i, PointerIcon.getSystemIcon(context, i));
        }
    }

    public void mouseClickInteraction(View view) {
        if (view == null) {
            Log.m32w(TAG, "mouseClickInteraction - view is null");
        } else {
            view.setOnTouchListener($$Lambda$MouseKeyboardProvider$bC0ScrjL1_AqEUaCb_5PfvLx6f8.INSTANCE);
        }
    }

    static /* synthetic */ boolean lambda$mouseClickInteraction$0(View view, MotionEvent motionEvent) {
        return motionEvent.getAction() == 0 && motionEvent.getButtonState() == 2;
    }

    public void mouseClickInteraction(Activity activity, AbsFragment absFragment, View view) {
        if (activity == null || absFragment == null || view == null) {
            Log.m32w(TAG, "mouseClickInteraction - null occur");
            return;
        }
        if (view instanceof RecyclerView) {
            ((RecyclerView) view).addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                public void onRequestDisallowInterceptTouchEvent(boolean z) {
                }

                public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                }

                public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                    if (DesktopModeProvider.isDesktopMode()) {
                        if (motionEvent.getAction() == 0 && motionEvent.getButtonState() == 2) {
                            Log.m26i(MouseKeyboardProvider.TAG, "show contextual menu");
                            int i = -1;
                            if (recyclerView instanceof RecyclerView) {
                                i = recyclerView.getChildLayoutPosition(recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY()));
                            }
                            MouseKeyboardProvider.this.setTouchPosition(i);
                            ContextMenuProvider.showContextMenu((int) motionEvent.getX(), (int) motionEvent.getY(), recyclerView);
                        } else if (MouseKeyboardProvider.this.isShiftPressed() && motionEvent.getButtonState() == 1) {
                            Log.m26i(MouseKeyboardProvider.TAG, "shift click + left click key operation");
                            MouseKeyboardProvider.this.setShiftPressed(false);
                            if (Settings.getIntSettings(Settings.KEY_LIST_MODE, 0) != 1 || DataRepository.getInstance().getCategoryRepository().isChildList()) {
                                int unused = MouseKeyboardProvider.this.movePosition = recyclerView.getChildLayoutPosition(recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY()));
                                MouseKeyboardProvider mouseKeyboardProvider = MouseKeyboardProvider.this;
                                mouseKeyboardProvider.setTouchPosition(mouseKeyboardProvider.movePosition);
                                long unused2 = MouseKeyboardProvider.this.moveId = recyclerView.getAdapter().getItemId(MouseKeyboardProvider.this.movePosition);
                                CheckedItemProvider.initCheckedList();
                                Iterator<Long> it = CursorProvider.getInstance().getCurrentIDs(MouseKeyboardProvider.this.moveId).iterator();
                                while (it.hasNext()) {
                                    CheckedItemProvider.toggle(it.next().longValue());
                                }
                                VoiceNoteObservable.getInstance().notifyObservers(6);
                            } else if (CursorProvider.getInstance().getCategoriesCount() > 0) {
                                int unused3 = MouseKeyboardProvider.this.movePosition = recyclerView.getChildLayoutPosition(recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY()));
                                MouseKeyboardProvider mouseKeyboardProvider2 = MouseKeyboardProvider.this;
                                mouseKeyboardProvider2.setTouchPosition(mouseKeyboardProvider2.movePosition);
                                VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.SHORTCUT_SHIFT_SELECT));
                            }
                        }
                    }
                    return false;
                }
            });
        } else {
            view.setOnTouchListener(new View.OnTouchListener() {
                public final boolean onTouch(View view, MotionEvent motionEvent) {
                    return MouseKeyboardProvider.this.lambda$mouseClickInteraction$1$MouseKeyboardProvider(view, motionEvent);
                }
            });
        }
        absFragment.registerForContextMenu(view);
    }

    public /* synthetic */ boolean lambda$mouseClickInteraction$1$MouseKeyboardProvider(View view, MotionEvent motionEvent) {
        if (DesktopModeProvider.isDesktopMode()) {
            if (motionEvent.getAction() == 0 && motionEvent.getButtonState() == 2) {
                Log.m26i(TAG, "show contextual menu");
                int i = -1;
                if (view instanceof ListView) {
                    i = ((ListView) view).pointToPosition((int) motionEvent.getX(), (int) motionEvent.getY());
                }
                setTouchPosition(i);
                ContextMenuProvider.showContextMenu((int) motionEvent.getX(), (int) motionEvent.getY(), view);
            } else if (isShiftPressed() && motionEvent.getButtonState() == 1) {
                Log.m26i(TAG, "shift click + left click key operation");
                setShiftPressed(false);
                if (Settings.getIntSettings(Settings.KEY_LIST_MODE, 0) != 1 || DataRepository.getInstance().getCategoryRepository().isChildList()) {
                    AbsListView absListView = (AbsListView) view;
                    this.movePosition = absListView.pointToPosition((int) motionEvent.getX(), (int) motionEvent.getY());
                    setTouchPosition(this.movePosition);
                    this.moveId = absListView.getItemIdAtPosition(this.movePosition);
                    CheckedItemProvider.initCheckedList();
                    Iterator<Long> it = CursorProvider.getInstance().getCurrentIDs(this.moveId).iterator();
                    while (it.hasNext()) {
                        CheckedItemProvider.toggle(it.next().longValue());
                    }
                    VoiceNoteObservable.getInstance().notifyObservers(6);
                } else if (CursorProvider.getInstance().getCategoriesCount() > 0) {
                    this.movePosition = ((ListView) view).pointToPosition((int) motionEvent.getX(), (int) motionEvent.getY());
                    setTouchPosition(this.movePosition);
                    VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.SHORTCUT_SHIFT_SELECT));
                }
            }
        }
        return false;
    }

    public void destroyMouseClickInteraction(AbsFragment absFragment, View view) {
        absFragment.unregisterForContextMenu(view);
    }
}
