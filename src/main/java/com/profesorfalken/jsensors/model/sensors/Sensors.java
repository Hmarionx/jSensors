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
package com.profesorfalken.jsensors.model.sensors;

import java.util.List;

/**
 *
 * @author javier
 */
public class Sensors {
    public final List<Temperature> temperatures;
    public final List<Fan> fans;

    public Sensors(List<Temperature> temperatures, List<Fan> fans) {
        this.temperatures = temperatures;
        this.fans = fans;
    }    
}
