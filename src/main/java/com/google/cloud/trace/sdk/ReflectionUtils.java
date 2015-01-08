// Copyright 2014 Google Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.cloud.trace.sdk;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Common reflection utilities.
 */
public class ReflectionUtils {

  private static final Logger logger = Logger.getLogger(ReflectionUtils.class.getName());

  /**
   * Reflectively instantiates and initializes a class using a Properties file.
   */
  public static Object createFromProperties(String className, Properties props) {
    Object obj = null;
    try {
      obj = Class.forName(className).newInstance();
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      logger.log(Level.WARNING, "Error creating " + className, e);
    }
    if (obj != null && obj instanceof CanInitFromProperties) {
      ((CanInitFromProperties) obj).initFromProperties(props);
    }
    return obj;
  }
}
