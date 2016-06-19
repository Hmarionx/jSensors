/*
 * Copyright 2016 Javier Garcia Alonso.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.profesorfalken.jsensors.manager.windows;

import com.profesorfalken.jsensors.manager.SensorsManager;
import com.profesorfalken.jsensors.manager.windows.powershell.PowerShellOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author javier
 */
public class WindowsSensorsManager extends SensorsManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(WindowsSensorsManager.class);

    private static final String LINE_BREAK = "\r\n";

    public String getSensorsData() {
        String rawSensorsData = PowerShellOperations.getRawSensorsData();

        if (debugMode) {
            LOGGER.info(rawSensorsData);
        }

        return normalizeSensorsData(rawSensorsData);
    }

    private static String normalizeSensorsData(String rawSensorsData) {
        StringBuilder normalizedSensorsData = new StringBuilder();
        String[] dataLines = rawSensorsData.split("\\r?\\n");

        boolean readingHardLabel = false;
        boolean readingSensor = false;
        for (final String dataLine : dataLines) {
            if (readingHardLabel == false && "HardwareType".equals(getKey(dataLine))) {
                String hardwareType = getValue(dataLine);
                if ("CPU".equals(hardwareType)) {
                    normalizedSensorsData.append("[COMPONENT]").append(LINE_BREAK);
                    normalizedSensorsData.append("CPU").append(LINE_BREAK);
                    readingHardLabel = true;
                    continue;
                } else if (hardwareType.toUpperCase().startsWith("GPU")) {
                    normalizedSensorsData.append("[COMPONENT]").append(LINE_BREAK);
                    normalizedSensorsData.append("GPU").append(LINE_BREAK);
                    readingHardLabel = true;
                    continue;
                } else if ("HDD".equals(hardwareType)) {
                    normalizedSensorsData.append("[COMPONENT]").append(LINE_BREAK);
                    normalizedSensorsData.append("DISK").append(LINE_BREAK);
                    readingHardLabel = true;
                    continue;
                }
            }

            if (readingHardLabel) {
                if ("Name".equals(getKey(dataLine))) {
                    normalizedSensorsData.append("Label: ").append(getValue(dataLine)).append(LINE_BREAK);
                    readingHardLabel = false;
                }
            } else {
                if ("SensorType".equals(getKey(dataLine))) {
                    String sensorType = getValue(dataLine);
                    if ("Temperature".equals(sensorType) || "Fan".equals(sensorType)) {
                        normalizedSensorsData.append("Temperature".equals(sensorType) ? "Temp " : "Fan ");
                        readingSensor = true;
                        continue;
                    }
                }
                if (readingSensor) {
                    if ("Name".equals(getKey(dataLine))) {
                        normalizedSensorsData.append(getValue(dataLine)).append(": ");
                    } else if ("Value".equals(getKey(dataLine))) {
                        normalizedSensorsData.append(getValue(dataLine)).append(LINE_BREAK);
                        readingSensor = false;
                    }
                }
            }
        }

        return normalizedSensorsData.toString();
    }

    private static String getKey(String line) {
        return getData(line, 0);
    }

    private static String getValue(String line) {
        return getData(line, 1);
    }

    private static String getData(String line, final int index) {
        if (line.indexOf(":") > 0) {
            return line.split(":", 2)[index].trim();
        }

        return "";
    }
}
