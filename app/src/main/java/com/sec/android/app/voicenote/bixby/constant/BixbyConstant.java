package com.sec.android.app.voicenote.bixby.constant;

public class BixbyConstant {

    public static class BixbyActions {
        public static final String ACTION_GET_RECORDED_FILE_COUNT = "viv.voiceRecorderApp.GetRecordedFileCount";
        public static final String ACTION_GET_RECORDING_INFO = "viv.voiceRecorderApp.GetRecordingInfo";
        public static final String ACTION_PLAY_RECORDING_FILE = "viv.voiceRecorderApp.PlayRecordingFile";
        public static final String ACTION_SPEECH_TO_TEXT_INFO = "viv.voiceRecorderApp.SpeechToTextInfo";
        public static final String ACTION_START_RECORDING = "viv.voiceRecorderApp.StartRecording";
        public static final String ACTION_START_TNC = "viv.voiceRecorderApp.StartTnc";
    }

    public static class BixbyStartMode {
        public static final String BIXBY_START_DATA = "bixbyStartData";
        public static final String BIXBY_START_DEFAULT = "bixbyStartDefault";
        public static final String BIXBY_START_PLAY = "bixbyStartPlay";
        public static final String BIXBY_START_RECORD = "bixbyStartRecord";
    }

    public static class InputParameter {
        public static final String BIXBY_FILE_NAME = "fileName";
        public static final String FILE_NAME_ID = "fileNameID";
        public static final String RECORDING_MODE = "recordingMode";
    }

    public static class RecordingMode {
        public static final String INTERVIEW = "interview";
        public static final String SPEECH_TO_TEXT = "speech-to-text";
        public static final String STANDARD = "standard";
    }

    public static class ResponseOutputParameter {
        public static final String CAUSE = "cause";
        public static final String CHINA_MODEL = "chinaModel";
        public static final String NETWORK_CONNECTION = "networkConnection";
        public static final String RECORDING_MODE = "recordingMode";
        public static final String STATUS = "status";
        public static final String SUPPORT_INTERVIEW_MODE = "supportInterviewMode";
        public static final String SUPPORT_SPEECH_TO_TEXT_MODE = "supportSpeechToTextMode";
        public static final String TNC = "tnc";
    }
}
