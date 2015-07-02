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
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

/**
 * @author David Mirly
 * @version 1.0
 * @since 6/10/15
 *
 * When the print button is clicked, this class handles printing.
 *
 */
class BusinessCardPrint implements Printable {

    private final BufferedImage mImage;

    /**
     *
     * @param inImage the 3.5" x 2" business card image, print as many of these as will fit on the page
     */
    public BusinessCardPrint(BufferedImage inImage)
    {
        mImage = inImage;
    }

    /**
     * User has OKed the print dialog after clicking the print button.  Draw as many cards as will
     * fit on whatever paper size chosen and print.
     *
     * @param g graphics context of printer page to render business cards to
     * @param pf tells useful information such as how big is the page we are printing to
     * @param page we only have one page, so this better be zero
     * @return PAGE_EXISTS is all goes well, NO_SUCH_PAGE if invalid page number
     * @throws PrinterException
     */
    public int print(Graphics g, PageFormat pf, int page) throws
            PrinterException {

        if (page > 0) {
            return NO_SUCH_PAGE;
        }

        double CARD_HEIGHT = 2;  // in inches
        double CARD_WIDTH = 3.5; // in inches
        double CARD_SPACING = 0.25; // 1/4 of an inch

        // translate the origin to the top leftmost printable point
        Graphics2D g2d = (Graphics2D)g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());

        // in 1/72's of an inch, only can print inside these dimensions (not in the margins)
        double pageWidth = pf.getImageableWidth();
        double pageHeight = pf.getImageableHeight();

        // ok, so given the page size, card size and spacing, how many can we fit?
        int cols = (int)(pageWidth / convertInchesToPixels(CARD_WIDTH + CARD_SPACING));
        int rows = (int)(pageHeight / convertInchesToPixels(CARD_HEIGHT + CARD_SPACING));

        int x = 0;
        int y = 0;

        for (int r = 0; r < rows; r++) {

            for (int c = 0; c < cols; c++) {
                g2d.drawImage(mImage, null, x, y);
                x += convertInchesToPixels(CARD_WIDTH);
                x += convertInchesToPixels(CARD_SPACING);
            }

            x = 0;
            y += convertInchesToPixels(CARD_HEIGHT);
            y += convertInchesToPixels(CARD_SPACING);

        }

        return PAGE_EXISTS;
    }

    /**
     * Converts input in inches (72 DPI) to pixels.
     * @param inches to convert to pixels
     * @return inches * 72 and round
     */
    private int convertInchesToPixels(double inches) {
       return (int)(inches * 72);
    }

    /**
     * Called when print button clicked.  Opens print dialog and if OKed, calls print(...)
     */
    public void doPrint() {

        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(this);
        boolean ok = job.printDialog();

        if (ok) {

            try {

                job.print();

            } catch (PrinterException pe) {

                pe.printStackTrace();

            }

        }
    }

}
