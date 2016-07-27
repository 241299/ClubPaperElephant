package ru.bibliowiki.litclubbs.util;

import android.content.Context;
/**
 * Copyright 2011 Oleg Elifantiev
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/**
 * Simple error reporting facility.
 * Saves stacktraces and exception information to external storage (if mounted and writable)
 * Files are saved to folder Android/data/your.package.name/files/stacktrace-dd-MM-YY.txt
 *
 * To apply error reporting simply do the following
 *   RoboErrorReporter.bindReporter(yourContext);
 */
public final class RoboErrorReporter {

    private RoboErrorReporter() {
    }

    /**
     * Apply error reporting to a specified application context
     * @param context context for which errors are reported (used to get package name)
     */
    public static void bindReporter(Context context) {
        Thread.setDefaultUncaughtExceptionHandler(UncaughtExceptionHandler.inContext(context));
    }

    public static void reportError(Context context, Throwable error) {
        UncaughtExceptionHandler.reportOnlyHandler(context).uncaughtException(Thread.currentThread(), error);
    }

}