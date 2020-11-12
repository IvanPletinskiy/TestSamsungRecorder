package com.sec.android.app.voicenote.service.helper;

import androidx.annotation.NonNull;
import java.util.Locale;

public class Bookmark implements Comparable<Bookmark> {
    private String description;
    private int elapsed;
    private boolean isNamed;
    private String title;

    public boolean getNamed() {
        return this.isNamed;
    }

    public void setNamed(boolean z) {
        this.isNamed = z;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String str) {
        this.title = str;
    }

    public int getElapsed() {
        return this.elapsed;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String str) {
        this.description = str;
    }

    public void setElapsed(int i) {
        this.elapsed = i;
    }

    public Bookmark(int i, String str, String str2, boolean z) {
        this.title = str;
        this.elapsed = i;
        this.description = str2;
        this.isNamed = z;
    }

    public Bookmark(Bookmark bookmark) {
        this.title = bookmark.title;
        this.elapsed = bookmark.elapsed;
        this.description = bookmark.description;
        this.isNamed = bookmark.isNamed;
    }

    public String toString() {
        return timeToStr((long) this.elapsed) + " - " + this.title + ' ' + this.description;
    }

    public int compareTo(@NonNull Bookmark bookmark) {
        return this.elapsed - bookmark.elapsed;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Bookmark)) {
            return false;
        }
        Bookmark bookmark = (Bookmark) obj;
        if (this.elapsed != bookmark.elapsed || this.isNamed != bookmark.isNamed || !this.title.equals(bookmark.title) || !this.description.equals(bookmark.description)) {
            return false;
        }
        return true;
    }

    private String timeToStr(long j) {
        long j2 = j / 1000;
        return String.format(Locale.US, "%02d:%02d:%02d", new Object[]{Integer.valueOf((int) (j2 / 3600)), Integer.valueOf((int) ((j2 / 60) % 60)), Integer.valueOf((int) (j2 % 60))});
    }
}
