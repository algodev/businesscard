/*
 * Copyright 2015 David Mirly
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mirly.businesscard;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * @author David Mirly
 * @version 1.0
 * @since 6/30/15
 *
 * In order to have control over the font selection, this class loads and uses the Gnu FreeFont FreeMono.ttf.
 * Specifically the 20120503 version.
 *
 * http://www.gnu.org/software/freefont/
 *
 * The license is GPL v3 or later.
 *
 * This font was selected because it demonstrated reliable fidelity of characters at small sizes.
 *
 * NOTE: It would be nice to not have to package a font and instead draw an image larger than a business card
 * and scale it down but there isn't an algorithm I found in java (including Java 2D) that does a decent job
 * with the text, although it is possible to get good results outside of java.  If the effort is warranted/needed
 * then it should be possible to implement a suitable scaling algorithm.
 *
 */
class TTF {

    private static final Font TTF;

    static {

        try {

            TTF = Font.createFont(Font.TRUETYPE_FONT, new File("FreeMono.ttf"));

        } catch (IOException | FontFormatException e) {

            e.printStackTrace();
            throw new RuntimeException("Could not load FreeMono TTF.");

        }

    }

    public static Font getFont(float ptsize) {

        return TTF.deriveFont(ptsize);

    }

}
