package com.sec.android.app.voicenote.p007ui;

import android.util.SparseArray;
import android.view.View;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.service.Engine;

/* renamed from: com.sec.android.app.voicenote.ui.ControlButtonFactory */
class ControlButtonFactory {
    private static final float ALPHA_DISABLE = 0.4f;
    private static final float ALPHA_ENABLE = 1.0f;
    private static final String TAG = "ControlButtonFactory";
    private static final SparseArray<AbsButton> mButtonFactory = new SparseArray<>();
    /* access modifiers changed from: private */
    public static final SparseArray<View> mViewFactory = new SparseArray<>();

    /* renamed from: com.sec.android.app.voicenote.ui.ControlButtonFactory$POSITION */
    static class POSITION {
        static final int CENTER = 2;
        static final int CENTER_END = 3;
        static final int CENTER_START = 1;
        static final int END = 4;
        static final int START = 0;

        POSITION() {
        }
    }

    ControlButtonFactory(View view) {
        setView(view);
    }

    public void clear() {
        mViewFactory.clear();
        mButtonFactory.clear();
    }

    public void setView(View view) {
        View view2 = view;
        mViewFactory.clear();
        mViewFactory.put(1009, view2.findViewById(C0690R.C0693id.controlbutton_record_start));
        mViewFactory.put(1001, view2.findViewById(C0690R.C0693id.controlbutton_record_start));
        mViewFactory.put(1002, view2.findViewById(C0690R.C0693id.controlbutton_record_pause));
        mViewFactory.put(1003, view2.findViewById(C0690R.C0693id.controlbutton_record_resume));
        mViewFactory.put(1004, view2.findViewById(C0690R.C0693id.controlbutton_record_save));
        mViewFactory.put(1007, view2.findViewById(C0690R.C0693id.controlbutton_pre_play));
        mViewFactory.put(1008, view2.findViewById(C0690R.C0693id.controlbutton_pre_pause));
        mViewFactory.put(Event.PLAY_START, view2.findViewById(C0690R.C0693id.controlbutton_play_start));
        mViewFactory.put(Event.PLAY_PAUSE, view2.findViewById(C0690R.C0693id.controlbutton_play_pause));
        mViewFactory.put(Event.PLAY_RW, view2.findViewById(C0690R.C0693id.controlbutton_play_prev));
        mViewFactory.put(Event.PLAY_FF, view2.findViewById(C0690R.C0693id.controlbutton_play_next));
        mViewFactory.put(Event.EDIT_PLAY_START, view2.findViewById(C0690R.C0693id.controlbutton_edit_play));
        mViewFactory.put(Event.EDIT_PLAY_PAUSE, view2.findViewById(C0690R.C0693id.controlbutton_edit_pause));
        mViewFactory.put(Event.EDIT_RECORD, view2.findViewById(C0690R.C0693id.controlbutton_edit_record_start));
        mViewFactory.put(Event.EDIT_RECORD_PAUSE, view2.findViewById(C0690R.C0693id.controlbutton_edit_record_pause));
        mViewFactory.put(Event.EDIT_TRIM, view2.findViewById(C0690R.C0693id.controlbutton_edit_trim_button));
        mViewFactory.put(Event.EDIT_TRIM_IN_PROGRESS, view2.findViewById(C0690R.C0693id.controlbutton_edit_trim_progress));
        mViewFactory.put(Event.TRANSLATION_START, view2.findViewById(C0690R.C0693id.controlbutton_translation_start));
        mViewFactory.put(Event.TRANSLATION_PAUSE, view2.findViewById(C0690R.C0693id.controlbutton_translation_pause));
        mViewFactory.put(Event.TRANSLATION_RESUME, view2.findViewById(C0690R.C0693id.controlbutton_translation_resume));
        mViewFactory.put(Event.TRANSLATION_SAVE, view2.findViewById(C0690R.C0693id.controlbutton_translation_save));
        mButtonFactory.clear();
        mButtonFactory.put(1009, new MainButton());
        mButtonFactory.put(4, new MainButton());
        mButtonFactory.put(Event.CHANGE_MODE, new MainButton());
        mButtonFactory.put(1010, new MainButton());
        mButtonFactory.put(1001, new RecordButton());
        mButtonFactory.put(1003, new RecordButton());
        mButtonFactory.put(1002, new RecordPauseButton());
        mButtonFactory.put(1004, new ListButton());
        mButtonFactory.put(1006, new MainButton());
        mButtonFactory.put(1007, new RecordPlayButton());
        mButtonFactory.put(1008, new RecordPauseButton());
        mButtonFactory.put(3, new ListButton());
        mButtonFactory.put(7, new ListButton());
        mButtonFactory.put(Event.MINI_PLAY_START, new ListButton());
        mButtonFactory.put(Event.MINI_PLAY_PAUSE, new ListButton());
        mButtonFactory.put(Event.MINI_PLAY_RESUME, new ListButton());
        mButtonFactory.put(Event.MINI_PLAY_NEXT, new ListButton());
        mButtonFactory.put(Event.MINI_PLAY_PREV, new ListButton());
        mButtonFactory.put(Event.PLAY_START, new PlayButton());
        mButtonFactory.put(Event.PLAY_PAUSE, new PlayPauseButton());
        mButtonFactory.put(Event.PLAY_RESUME, new PlayButton());
        mButtonFactory.put(Event.PLAY_NEXT, new PlayButton());
        mButtonFactory.put(Event.PLAY_PREV, new PlayButton());
        mButtonFactory.put(5, new EditButton());
        mButtonFactory.put(Event.EDIT_PLAY_START, new EditPlayButton());
        mButtonFactory.put(Event.EDIT_PLAY_PAUSE, new EditButton());
        mButtonFactory.put(Event.EDIT_PLAY_RESUME, new EditPlayButton());
        mButtonFactory.put(Event.EDIT_RECORD, new EditRecordButton());
        mButtonFactory.put(Event.EDIT_RECORD_PAUSE, new EditButton());
        mButtonFactory.put(Event.EDIT_RECORD_SAVE, new EditButton());
        mButtonFactory.put(1, new EmptyButton());
        mButtonFactory.put(Event.EDIT_TRIM, new EditTrimButton());
        mButtonFactory.put(17, new TranslationButton());
        mButtonFactory.put(Event.TRANSLATION_START, new TranslationStartButton());
        mButtonFactory.put(Event.TRANSLATION_PAUSE, new TranslationPauseButton());
        mButtonFactory.put(Event.TRANSLATION_RESUME, new TranslationStartButton());
        mButtonFactory.put(Event.TRANSLATION_SAVE, new TranslationPauseButton());
    }

    public View getView(int i) {
        return mViewFactory.get(i);
    }

    /* access modifiers changed from: package-private */
    public AbsButton getButtons(int i) {
        return mButtonFactory.get(i, (AbsButton) null);
    }

    /* access modifiers changed from: package-private */
    public void enableButton(View view, boolean z) {
        if (view != null) {
            if (z) {
                view.setAlpha(1.0f);
//                view.semSetHoverPopupType(1);
            } else {
                view.setAlpha(ALPHA_DISABLE);
//                view.semSetHoverPopupType(0);
            }
            view.setEnabled(z);
            view.setFocusable(z);
        }
    }

    /* access modifiers changed from: package-private */
    public void blockAllButton(boolean z) {
        int size = mViewFactory.size();
        for (int i = 0; i < size; i++) {
            blockButton(mViewFactory.valueAt(i), z);
        }
    }

    private void blockButton(View view, boolean z) {
        if (view != null) {
            Log.m26i(TAG, "blockButton : " + view.getContentDescription() + " enabled : " + z);
            switch (view.getId()) {
                case C0690R.C0693id.controlbutton_edit_record_start:
                    if (z || !Engine.getInstance().isEditRecordable()) {
                        enableButton(view, false);
                        return;
                    } else {
                        enableButton(view, true);
                        return;
                    }
                case C0690R.C0693id.controlbutton_edit_trim_button:
                    if (z || (!Engine.getInstance().isTrimEnable() && !Engine.getInstance().isDeleteEnable())) {
                        enableButton(view, false);
                        return;
                    } else {
                        enableButton(view, true);
                        return;
                    }
                case C0690R.C0693id.controlbutton_record_resume:
                    if (z || !Engine.getInstance().isEditRecordable()) {
                        enableButton(view, false);
                        return;
                    } else {
                        enableButton(view, true);
                        return;
                    }
                case C0690R.C0693id.controlbutton_translation_start:
                    view.setClickable(!z);
                    return;
                default:
                    view.setEnabled(!z);
                    view.setFocusable(!z);
                    return;
            }
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.ControlButtonFactory$MainButton */
    private class MainButton extends AbsButton {
        MainButton() {
            super();
            this.mViewHolder.put(2, ControlButtonFactory.mViewFactory.get(1001));
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.ControlButtonFactory$RecordButton */
    private class RecordButton extends AbsButton {
        RecordButton() {
            super();
            this.mViewHolder.put(0, ControlButtonFactory.mViewFactory.get(1007));
            this.mViewHolder.put(2, ControlButtonFactory.mViewFactory.get(1002));
            this.mViewHolder.put(4, ControlButtonFactory.mViewFactory.get(1004));
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.ControlButtonFactory$RecordPauseButton */
    private class RecordPauseButton extends AbsButton {
        RecordPauseButton() {
            super();
            this.mViewHolder.put(0, ControlButtonFactory.mViewFactory.get(1007));
            this.mViewHolder.put(2, ControlButtonFactory.mViewFactory.get(1003));
            this.mViewHolder.put(4, ControlButtonFactory.mViewFactory.get(1004));
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.ControlButtonFactory$RecordPlayButton */
    private class RecordPlayButton extends AbsButton {
        RecordPlayButton() {
            super();
            this.mViewHolder.put(0, ControlButtonFactory.mViewFactory.get(1008));
            this.mViewHolder.put(2, ControlButtonFactory.mViewFactory.get(1003));
            this.mViewHolder.put(4, ControlButtonFactory.mViewFactory.get(1004));
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.ControlButtonFactory$ListButton */
    private class ListButton extends AbsButton {
        ListButton() {
            super();
            this.mViewHolder.put(2, ControlButtonFactory.mViewFactory.get(1001));
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.ControlButtonFactory$PlayButton */
    private class PlayButton extends AbsButton {
        PlayButton() {
            super();
            this.mViewHolder.put(1, ControlButtonFactory.mViewFactory.get(Event.PLAY_RW));
            this.mViewHolder.put(2, ControlButtonFactory.mViewFactory.get(Event.PLAY_PAUSE));
            this.mViewHolder.put(3, ControlButtonFactory.mViewFactory.get(Event.PLAY_FF));
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.ControlButtonFactory$PlayPauseButton */
    private class PlayPauseButton extends AbsButton {
        PlayPauseButton() {
            super();
            this.mViewHolder.put(1, ControlButtonFactory.mViewFactory.get(Event.PLAY_RW));
            this.mViewHolder.put(2, ControlButtonFactory.mViewFactory.get(Event.PLAY_START));
            this.mViewHolder.put(3, ControlButtonFactory.mViewFactory.get(Event.PLAY_FF));
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.ControlButtonFactory$EditButton */
    private class EditButton extends AbsButton {
        EditButton() {
            super();
            this.mViewHolder.put(0, ControlButtonFactory.mViewFactory.get(Event.EDIT_RECORD));
            this.mViewHolder.put(2, ControlButtonFactory.mViewFactory.get(Event.EDIT_PLAY_START));
            this.mViewHolder.put(4, ControlButtonFactory.mViewFactory.get(Event.EDIT_TRIM));
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.ControlButtonFactory$EditTrimButton */
    private class EditTrimButton extends AbsButton {
        EditTrimButton() {
            super();
            this.mViewHolder.put(0, ControlButtonFactory.mViewFactory.get(Event.EDIT_RECORD));
            this.mViewHolder.put(2, ControlButtonFactory.mViewFactory.get(Event.EDIT_PLAY_START));
            this.mViewHolder.put(4, ControlButtonFactory.mViewFactory.get(Event.EDIT_TRIM_IN_PROGRESS));
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.ControlButtonFactory$EditPlayButton */
    private class EditPlayButton extends AbsButton {
        EditPlayButton() {
            super();
            this.mViewHolder.put(0, ControlButtonFactory.mViewFactory.get(Event.EDIT_RECORD));
            this.mViewHolder.put(2, ControlButtonFactory.mViewFactory.get(Event.EDIT_PLAY_PAUSE));
            this.mViewHolder.put(4, ControlButtonFactory.mViewFactory.get(Event.EDIT_TRIM));
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.ControlButtonFactory$EditRecordButton */
    private class EditRecordButton extends AbsButton {
        EditRecordButton() {
            super();
            this.mViewHolder.put(0, ControlButtonFactory.mViewFactory.get(Event.EDIT_RECORD_PAUSE));
            this.mViewHolder.put(2, ControlButtonFactory.mViewFactory.get(Event.EDIT_PLAY_START));
            this.mViewHolder.put(4, ControlButtonFactory.mViewFactory.get(Event.EDIT_TRIM));
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.ControlButtonFactory$TranslationButton */
    private class TranslationButton extends AbsButton {
        TranslationButton() {
            super();
            this.mViewHolder.put(2, ControlButtonFactory.mViewFactory.get(Event.TRANSLATION_START));
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.ControlButtonFactory$TranslationStartButton */
    private class TranslationStartButton extends AbsButton {
        TranslationStartButton() {
            super();
            this.mViewHolder.put(2, ControlButtonFactory.mViewFactory.get(Event.TRANSLATION_PAUSE));
            this.mViewHolder.put(4, ControlButtonFactory.mViewFactory.get(Event.TRANSLATION_SAVE));
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.ControlButtonFactory$TranslationPauseButton */
    private class TranslationPauseButton extends AbsButton {
        TranslationPauseButton() {
            super();
            this.mViewHolder.put(2, ControlButtonFactory.mViewFactory.get(Event.TRANSLATION_RESUME));
            this.mViewHolder.put(4, ControlButtonFactory.mViewFactory.get(Event.TRANSLATION_SAVE));
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.ControlButtonFactory$EmptyButton */
    private class EmptyButton extends AbsButton {
        EmptyButton() {
            super();
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.ControlButtonFactory$AbsButton */
    abstract class AbsButton {
        final SparseArray<View> mViewHolder = new SparseArray<>();

        AbsButton() {
            this.mViewHolder.clear();
        }

        public View get(int i) {
            return this.mViewHolder.get(i);
        }

        public void remove(int i) {
            this.mViewHolder.remove(i);
        }

        public void remove(View view) {
            int indexOfValue = this.mViewHolder.indexOfValue(view);
            if (indexOfValue != -1) {
                this.mViewHolder.removeAt(indexOfValue);
            }
        }

        /* access modifiers changed from: package-private */
        public void removeAll() {
            this.mViewHolder.clear();
        }

        public void add(int i, int i2) {
            if (this.mViewHolder.get(i2) != ControlButtonFactory.mViewFactory.get(i)) {
                this.mViewHolder.put(4, ControlButtonFactory.mViewFactory.get(i));
            }
        }

        public int size() {
            return this.mViewHolder.size();
        }
    }
}
