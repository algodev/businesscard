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


import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author David Mirly
 * @version 1.0
 * @since 3/31/15
 *
 * A (perhaps) clever business card generator that prints stylized "punches" for each letter in your email address (or
 * whatever text you type in the top text field).
 *
 * No, this is not the encoding any old punch cards used that I am aware of, instead it is 7-bit ASCII with the low
 * bit just under the letter in the email address which appears at the very top of the card.
 *
 * There is also an optional text line at the bottom if you want.
 *
 * Since the punches are strictly 7 bit, you are restricted in what characters will encode properly.
 *
 * Also be careful to not use either too long of an email address or the optional text line at the bottom, otherwise
 * the font size will be so small that it will be illegable.  At this point, an email address of about 20 or so chars
 * is about all that's feasible.  The text at the bottom should not exceed this limit by too much.  You'll see in the
 * UI if what you have is too long.  The image font size will be too small.
 *
 * Also, the email address and punches will not start printing at the far left to allow for the upper right corner
 * to be clipped off like a punch card it.  There are markers for the card corners and this upper right slash that
 * will be printed.
 *
 * When printing, the maximum possible cards will be printed for the page size you select in the print dialog, including
 * some spacing between each card.
 *
 * Get stiff card stock that is colored like a typical punch card (pale yellow) at an office supply store.
 */

public class BusinessCard extends JApplet {

    public static void main( String args[] ) {

        final JFrame frame = new JFrame();
        JApplet applet = new BusinessCard();
        applet.init();

        // the total size of the java app including all UI
        int WIDTH = 600;
        int HEIGHT = 400;

        frame.setContentPane(applet.getContentPane());
        frame.setBounds(50, 50, WIDTH, HEIGHT);
        frame.setTitle("Punch Card Style Business Card Generator");
        frame.setVisible( true );

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        frame.addWindowListener(

                new WindowAdapter() {

                    public void windowClosed( WindowEvent we ) {
                        System.exit( 0 );
                    }

                }
        );

    }

    public void init() {

        Container contentPane = getContentPane();

        BusinessCardCanvas canvas = new BusinessCardCanvas();
        canvas.setBackground(Color.WHITE);

        BusinessCardUI ui = new BusinessCardUI(canvas);

        contentPane.add( ui.getTitleInputPanel(), BorderLayout.NORTH ); // text field for email address

        // the JPanel noise is to get the BusinessCardCanvas to center vertically and horizontally
        // in the BorderLayout.CENTER
        JPanel canvasPanel = new JPanel();
        canvasPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        canvasPanel.add(canvas);
        contentPane.add( canvasPanel, BorderLayout.CENTER );

        contentPane.add( ui.getBottomTextInputPanel(), BorderLayout.SOUTH ); // text field for optional bottom line

    }

}
