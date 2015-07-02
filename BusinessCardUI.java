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
import java.awt.event.*;
import java.awt.image.BufferedImage;

/**
 * @author David Mirly
 * @version 1.0
 * @since 4/1/15
 *
 * Provides the UI input text fields and print button and accepts text input and acts on the print button being clicked.
 *
 */
class BusinessCardUI extends KeyAdapter implements ActionListener {

    private final BusinessCardCanvas mCanvas;
    private JTextField mTitle;
    private JTextField mBottomField;
    private JLabel only7bit;

    public BusinessCardUI(BusinessCardCanvas inCanvas) {

       mCanvas = inCanvas;

    }

    /**
     * Called when user enters text in either the top or bottom text field.
     * If a non-7bit char is entered in the top line, it is eaten and a warning message is set in the UI.
     * @param ke what did they enter?
     */
    public void keyTyped(KeyEvent ke) {

        String title = mTitle.getText();
        String bottom = mBottomField.getText();
        only7bit.setText(" ");

        // don't append control chars (only act on them), FreeMono.ttf prints them out as boxed question marks.
        if (!Character.isISOControl(ke.getKeyChar())) {

            if (ke.getSource() == mTitle) {

                if (ke.getKeyChar() > 127) {

                    only7bit.setText("Only 7 bit characters allowed.");
                    ke.consume();

                } else {

                    title = title + ke.getKeyChar();

                }

            } else {

                bottom = bottom + ke.getKeyChar();

            }

        }

        mCanvas.redraw(title, bottom); // draw new business card image based on updated text fields


    }

    JPanel getTitleInputPanel() {

        JPanel tiPanel = new JPanel();
        tiPanel.setLayout(new GridLayout(2, 1, 10, 10)); // two rows, 1 col, h and v gap 10 pixels

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new FlowLayout());

        JLabel textLabel = new JLabel("Enter title text.");
        textPanel.add(textLabel);

        mTitle = new JTextField(30);
        mTitle.addKeyListener(this);
        textPanel.add(mTitle);

        tiPanel.add(textPanel);

        JPanel statusLine = new JPanel();
        only7bit = new JLabel(" ");
        statusLine.add(only7bit, BorderLayout.CENTER);
        tiPanel.add(statusLine);

        return tiPanel;

    }

    JPanel getBottomTextInputPanel() {

        JPanel botPanel = new JPanel();
        botPanel.setLayout(new GridLayout(2, 1, 10, 10)); // two rows, 1 col, h and v gap 10 pixels

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new FlowLayout());

        JLabel textLabel = new JLabel("Enter bottom text.  (optional)");
        textPanel.add(textLabel);

        mBottomField = new JTextField(30);
        mBottomField.addKeyListener(this);
        textPanel.add(mBottomField);

        botPanel.add(textPanel);

        JPanel buttonPanel = new JPanel();
        JButton print = new JButton("Print");
        print.addActionListener(this);
        print.setPreferredSize(new Dimension(50, 20));
        buttonPanel.add(print);
        botPanel.add(buttonPanel);

        return botPanel;

    }

    /**
     * User clicked the print button!  Yay!
     * @param ae only action we have is the print button
     */
    public void actionPerformed(ActionEvent ae) {

        // get the image of the business card
        BufferedImage image = new BufferedImage(BusinessCardCanvas.BC_WIDTH, BusinessCardCanvas.BC_HEIGHT, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2 = image.createGraphics();
        mCanvas.paintBusinessCard(g2, new Dimension(BusinessCardCanvas.BC_WIDTH, BusinessCardCanvas.BC_HEIGHT));
        g2.dispose();

        // and print it
        BusinessCardPrint bcp = new BusinessCardPrint(image);
        bcp.doPrint();


    }

}
