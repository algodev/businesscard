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
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;

/**
 * @author David Mirly
 * @version 1.0
 * @since 4/5/15
 *
 * Draws the business card image on the graphics context supplied.
 */
class BusinessCardCanvas extends JPanel {

    // The size of the canvas (BusinessCardCanvas) to be that of a standard (U.S.?) business card.
    // What you see on the screen is what it will look like when printed.
    static final int BC_WIDTH = 252;  // pixels for 3.5" @ 72 DPI
    static final int BC_HEIGHT = 144; // pixels for 2" @ 72 DPI

    // for drawing the business card image
    private static final double CHAR_SPACING = 1.5; // columns per title character. value greater than one puts extra horizontal spacing between each top char and its punches
    private static final double CORNER_PERCENTAGE = 0.10; // percentage of the card width taken by the upper left corner cut

    private String mTitleText = "";
    private String mBottomText = "";

    public Dimension getPreferredSize() {

        return new Dimension(BC_WIDTH, BC_HEIGHT);

    }

    /**
     * This essentially just calls paintBusinessCard so entities other the AWT can get a bc image.
     * @see BusinessCardUI
     * @param g context to draw to
     */
    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        paintBusinessCard(g, getSize());

    }

    /**
     * Where all the magic happens.  This method is what actually does the drawing of the business card image.
     * @param g graphics context to render to
     * @param bcDim how big the business card is in pixels
     */
    public void paintBusinessCard(Graphics g, Dimension bcDim) {

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, (int)bcDim.getWidth(), (int)bcDim.getHeight());

        g.setColor(Color.BLACK);

        // upper left corner cut line
        int cornerXY = (int)(CORNER_PERCENTAGE * bcDim.getWidth());
        g.drawLine(0, cornerXY, cornerXY, 0);


        // corner markers
        g.drawLine(0, 0, 0, 10);
        g.drawLine(0, 0, 10, 0);

        int w = (int)bcDim.getWidth();
        int h = (int)bcDim.getHeight();

        g.drawLine(0, h, 0, h-10);
        g.drawLine(0, h-1, 10, h-1);

        g.drawLine(w, 0, w-10, 0);
        g.drawLine(w-1, 0, w-1, 10);

        g.drawLine(w, h-1, w-10, h-1);
        g.drawLine(w-1, h, w-1, h-10);
        // end corner markers



        // find the biggest font possible given the input text.
        g.setFont( findFont(bcDim, ( (Graphics2D) g ).getFontRenderContext() ) );

        // ok, we have a font of appropriate size, so get the needed metrics from that font.
        LineMetrics topLM = g.getFont().getLineMetrics(mTitleText, ((Graphics2D) g).getFontRenderContext());
        LineMetrics bottomLM = g.getFont().getLineMetrics(mBottomText, ((Graphics2D) g).getFontRenderContext());
        double tallestChar = Math.max(topLM.getHeight(), bottomLM.getHeight());

        Rectangle2D charBounds = g.getFont().getMaxCharBounds( ((Graphics2D) g).getFontRenderContext() );
        int widestChar = (int)charBounds.getWidth();


        int titleLen = mTitleText.length();
        int titleCharSpacing = (int)(widestChar * CHAR_SPACING);  // pixels per title character
        int titleColStart = (int) ( ( ( bcDim.width - ( bcDim.width * CORNER_PERCENTAGE ) ) / 2) - (( titleLen / 2) * (titleCharSpacing)) + ( bcDim.width * CORNER_PERCENTAGE ) );

        // ok, we know all of the needed metrics and have are font, we are ready to draw.  One char at a time as well as the punches beneath that char.
        for (int i = 0; i < mTitleText.length(); i++) {

           punchLetter(mTitleText.charAt(i), i, titleColStart, titleCharSpacing, tallestChar, charBounds.getWidth(), bcDim.getHeight(), topLM, g);

        }

        // and finally the optional text at the bottom of the card
        int bottomLen = mBottomText.length();
        int bottomCol = (bcDim.width / 2) - ((bottomLen/2)*widestChar);
        g.drawString(mBottomText, bottomCol, (int)(bcDim.getHeight() - bottomLM.getDescent()) );

    }

    /**
     * Uses the size of the input text and the business card size to find the biggest font possible that will still
     * fit on the card given the following criteria:
     * <ul>The email address (top line) and punches can not start any farther left than the upper left corner cut (CORNER_PERCENTAGE)
     * <ul>There are 9 rows (the two text lines and 7 punches for each bit in a char)
     * <ul>There is a spacing added between each char in the top text line as well as the punches (CHAR_SPACING)
     *
     * @param cardDim dimensions of the overall business card
     * @param frc used to get font metrics
     * @see TTF
     * @return biggest Font that will fit within all the restrictions.
     */
    private Font findFont(Dimension cardDim, FontRenderContext frc) {

        int ptSize = 1;

        int titleCols = (int)(mTitleText.length() * CHAR_SPACING);

        // which text line is longer?  The bottom one shouldn't be too much longer than the top, but that's up to the user.
        // If they like what they see then swell.
        int maxNumCols = Math.max(titleCols, mBottomText.length());

        Font f = TTF.getFont(ptSize);
        LineMetrics topLM = f.getLineMetrics(mTitleText, frc);
        LineMetrics bottomLM = f.getLineMetrics(mBottomText, frc);

        double maxCharHeight = Math.max(topLM.getHeight(), bottomLM.getHeight()); // what is the height of the tallest char?
        double maxCharWidth = f.getMaxCharBounds(frc).getWidth(); // what is the width of the widest char?

        // need top row, 7 rows for bits, bottom row: total 9 rows
        double maxCharWidthAllowed = ( cardDim.getWidth() * (1 - CORNER_PERCENTAGE) ) / maxCharWidth;
        double maxRowHeight = cardDim.getHeight()/9;
        double maxWidthInPixelsWithCurrentFontsize = maxCharWidth * maxNumCols;

        // get as big of a font as possible
        while (maxCharHeight < maxRowHeight && maxCharWidth < maxCharWidthAllowed && maxWidthInPixelsWithCurrentFontsize < ( cardDim.getWidth() * ( 1 - CORNER_PERCENTAGE) ) ) {
            ptSize++;
            f = TTF.getFont(ptSize);
            topLM = f.getLineMetrics(mTitleText, frc);
            bottomLM = f.getLineMetrics(mBottomText, frc);

            maxCharHeight = Math.max(topLM.getHeight(), bottomLM.getHeight());
            maxCharWidth = f.getMaxCharBounds(frc).getWidth();
            maxWidthInPixelsWithCurrentFontsize = maxCharWidth * maxNumCols;
        }

        // the while loop will exit with a point size one too large.
        return TTF.getFont(ptSize - 1);

    }

    /**
     * Draw a char and up to 7 punches (rectangles) underneath representing the ASCII coding of the char.
     * @param c the char to draw
     * @param columnOffset which char in the top text string we are drawing
     * @param colStart where on the image to start drawing, columnwise
     * @param titleCharSpacing how much extra spacing between each char
     * @param maxCharHeight largest char height we have to draw
     * @param maxCharWidth largest char width we have to draw
     * @param cardHeight total card (image) height
     * @param topLM gets the baseline for the top text line
     * @param g the graphics context to draw to
     */
    private void punchLetter(char c, int columnOffset, int colStart, int titleCharSpacing, double maxCharHeight, double maxCharWidth, double cardHeight, LineMetrics topLM, Graphics g) {

        int col = colStart + (columnOffset*(titleCharSpacing)); // where do the char and punches start columnwise
        double punchSpaceTotal = cardHeight - (3 * maxCharHeight); // vertical spacing available for all 7 possible punches
        int punchAdvance = (int)(punchSpaceTotal / 7); // row increment between punches

        // the dimensions of a punch should be less than most chars and more tall than wide
        int punchHeight = (int)(maxCharHeight * 0.75);
        double punchWidth =  titleCharSpacing * 0.25; // keep double to reduce loss of precision in computing punchCol below

        int punchCol = col + (int)(maxCharWidth/2 - punchWidth/2); // where to start a punch at column wise

        g.drawString(Character.toString(c), col, (int)topLM.getAscent()); // draw the current char of the top line

        // draw the punches
        char mask = 0x1; // start with the low order bit
        int row = (int)(maxCharHeight*1.5); // start the first punch lower than the top text line by 1.5 times

        for (int bit = 0; bit < 7; bit++) {

            // if the bit is set, punch it!
            if ( (c & mask) != 0) {

                g.fillRect(punchCol, row, (int)punchWidth, punchHeight);

            }

            // go to next bit
            mask = (char)(mask << 1);
            row += punchAdvance;

        }

    }

    /**
     * Called each time one of the two text fields is modified to redraw the business card image.
     * @param titleText top line
     * @param bottomText bottom line
     */
    void redraw(String titleText, String bottomText) {

        mTitleText = titleText;
        mBottomText = bottomText;
        repaint();

    }

}
