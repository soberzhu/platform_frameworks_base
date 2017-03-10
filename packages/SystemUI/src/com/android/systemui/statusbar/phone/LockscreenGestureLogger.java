/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.android.systemui.statusbar.phone;

import android.metrics.LogMaker;
import android.util.ArrayMap;

import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.systemui.EventLogConstants;
import com.android.systemui.EventLogTags;

/**
 * Wrapper that emits both new- and old-style gesture logs.
 * TODO: delete this once the old logs are no longer needed.
 */
public class LockscreenGestureLogger {
    private ArrayMap<Integer, Integer> mLegacyMap;
    private LogMaker mLogMaker = new LogMaker(MetricsEvent.VIEW_UNKNOWN)
            .setType(MetricsEvent.TYPE_ACTION);
    private MetricsLogger mMetricsLogger = new MetricsLogger();

    public LockscreenGestureLogger() {
        mLegacyMap = new ArrayMap<>(EventLogConstants.METRICS_GESTURE_TYPE_MAP.length);
        for (int i = 0; i < EventLogConstants.METRICS_GESTURE_TYPE_MAP.length ; i++) {
            mLegacyMap.put(EventLogConstants.METRICS_GESTURE_TYPE_MAP[i], i);
        }
    }

    public void write(int gesture, int length, int velocity) {
        mMetricsLogger.write(mLogMaker.setCategory(gesture)
                .setType(MetricsEvent.TYPE_ACTION)
                .addTaggedData(MetricsEvent.FIELD_GESTURE_LENGTH, length)
                .addTaggedData(MetricsEvent.FIELD_GESTURE_VELOCITY, velocity));
        // also write old-style logs for backward-0compatibility
        EventLogTags.writeSysuiLockscreenGesture(safeLookup(gesture), length, velocity);
    }

    private int safeLookup(int gesture) {
        Integer value = mLegacyMap.get(gesture);
        if (value == null) {
            return MetricsEvent.VIEW_UNKNOWN;
        }
        return value;
    }

    @VisibleForTesting
    void setMetricsLogger(MetricsLogger metricsLogger) {
        mMetricsLogger = metricsLogger;
    }
}
