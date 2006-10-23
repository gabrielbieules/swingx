/*
 * $Id$
 *
 * Dual-licensed under LGPL (Sun and Romain Guy) and BSD (Romain Guy).
 *
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * Copyright (c) 2006 Romain Guy <romain.guy@mac.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.jdesktop.swingx.graphics;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * <p>A shadow renderer generates a drop shadow for any given picture, respecting
 * the transparency channel if present. The resulting picture contains the
 * shadow only and to create a drop shadow effect you will need to stack the
 * original picture and the shadow generated by the renderer.</p>
 * <h2>Shadow Properties</h2>
 * <p>A shadow is defined by three properties:
 * <ul>
 *   <li><i>size</i>: The size, in pixels, of the shadow. This property also
 *   defines the fuzzyness.</li>
 *   <li><i>opacity</i>: The opacity, between 0.0 and 1.0, of the shadow.</li>
 *   <li><i>color</i>: The color of the shadow. Shadows are not meant to be
 *   black only.</li>
 * </ul>
 * You can set these properties using the provided mutaters or the appropriate
 * constructor. Here are two ways of creating a green shadow of size 10 and
 * with an opacity of 50%:
 * <pre>
 * ShadowRenderer renderer = new ShadowRenderer(10, 0.5f, Color.GREEN);
 * // ..
 * renderer = new ShadowRenderer();
 * renderer.setSize(10);
 * renderer.setOpacity(0.5f);
 * renderer.setColor(Color.GREEN);
 * </pre>
 * The default constructor provides the following default values:
 * <ul>
 *   <li><i>size</i>: 5 pixels</li>
 *   <li><i>opacity</i>: 50%</li>
 *   <li><i>color</i>: Black</li>
 * </ul></p>
 * <h2>Generating a Shadow</h2>
 * <p>A shadow is generated as a <code>BufferedImage</code> from another
 * <code>BufferedImage</code>. Once the renderer is set up, you must call
 * {@link #createShadow} to actually generate the shadow:
 * <pre>
 * ShadowRenderer renderer = new ShadowRenderer();
 * // renderer setup
 * BufferedImage shadow = renderer.createShadow(bufferedImage);
 * </pre></p>
 * <p>The generated image dimensions are computed as following:</p>
 * <pre>
 * width  = imageWidth  + 2 * shadowSize
 * height = imageHeight + 2 * shadowSize
 * </pre>
 * <h2>Properties Changes</h2>
 * <p>This renderer allows to register property change listeners with
 * {@link #addPropertyChangeListener}. Listening to properties changes is very
 * useful when you emebed the renderer in a graphical component and give the API
 * user the ability to access the renderer. By listening to properties changes,
 * you can easily repaint the component when needed.</p>
 * <h2>Threading Issues</h2>
 * <p><code>ShadowRenderer</code> is not guaranteed to be thread-safe.</p>
 * 
 * @author Romain Guy <romain.guy@mac.com>
 */
public class ShadowRenderer {
    /**
     * <p>Identifies a change to the size used to render the shadow.</p>
     * <p>When the property change event is fired, the old value and the new
     * value are provided as <code>Integer</code> instances.</p>
     */
    public static final String SIZE_CHANGED_PROPERTY = "shadow_size";
    
    /**
     * <p>Identifies a change to the opacity used to render the shadow.</p>
     * <p>When the property change event is fired, the old value and the new
     * value are provided as <code>Float</code> instances.</p>
     */
    public static final String OPACITY_CHANGED_PROPERTY = "shadow_opacity";
    
    /**
     * <p>Identifies a change to the color used to render the shadow.</p>
     */
    public static final String COLOR_CHANGED_PROPERTY = "shadow_color";

    // size of the shadow in pixels (defines the fuzziness)
    private int size = 5;
    
    // opacity of the shadow
    private float opacity = 0.5f;
    
    // color of the shadow
    private Color color = Color.BLACK;
    
    // notifies listeners of properties changes
    private PropertyChangeSupport changeSupport;

    /**
     * <p>Creates a default good looking shadow generator.
     * The default shadow renderer provides the following default values:
     * <ul>
     *   <li><i>size</i>: 5 pixels</li>
     *   <li><i>opacity</i>: 50%</li>
     *   <li><i>color</i>: Black</li>
     * </ul></p>
     * <p>These properties provide a regular, good looking shadow.</p>
     */
    public ShadowRenderer() {
        this(5, 0.5f, Color.BLACK);
    }
    
    /**
     * <p>A shadow renderer needs three properties to generate shadows.
     * These properties are:</p> 
     * <ul>
     *   <li><i>size</i>: The size, in pixels, of the shadow. This property also
     *   defines the fuzzyness.</li>
     *   <li><i>opacity</i>: The opacity, between 0.0 and 1.0, of the shadow.</li>
     *   <li><i>color</i>: The color of the shadow. Shadows are not meant to be
     *   black only.</li>
     * </ul>
     * @param size the size of the shadow in pixels. Defines the fuzziness.
     * @param opacity the opacity of the shadow.
     * @param color the color of the shadow.
     */
    public ShadowRenderer(final int size, final float opacity, final Color color) {
        //noinspection ThisEscapedInObjectConstruction
        changeSupport = new PropertyChangeSupport(this);

        setSize(size);
        setOpacity(opacity);
        setColor(color);
    }

    /**
     * <p>Add a PropertyChangeListener to the listener list. The listener is
     * registered for all properties. The same listener object may be added
     * more than once, and will be called as many times as it is added. If
     * <code>listener</code> is null, no exception is thrown and no action
     * is taken.</p> 
     * @param listener the PropertyChangeListener to be added
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    /**
     * <p>Remove a PropertyChangeListener from the listener list. This removes
     * a PropertyChangeListener that was registered for all properties. If
     * <code>listener</code> was added more than once to the same event source,
     * it will be notified one less time after being removed. If
     * <code>listener</code> is null, or was never added, no exception is thrown
     * and no action is taken.</p>
     * @param listener the PropertyChangeListener to be removed
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    /**
     * <p>Gets the color used by the renderer to generate shadows.</p>
     * @return this renderer's shadow color
     */
    public Color getColor() {
        return color;
    }

    /**
     * <p>Sets the color used by the renderer to generate shadows.</p>
     * <p>Consecutive calls to {@link #createShadow} will all use this color
     * until it is set again.</p>
     * <p>If the color provided is null, the previous color will be retained.</p>
     * @param shadowColor the generated shadows color
     */
    public void setColor(final Color shadowColor) {
        if (shadowColor != null) {
            Color oldColor = this.color;
            this.color = shadowColor;
            changeSupport.firePropertyChange(COLOR_CHANGED_PROPERTY,
                                             oldColor,
                                             this.color);
        }
    }

    /**
     * <p>Gets the opacity used by the renderer to generate shadows.</p>
     * <p>The opacity is comprised between 0.0f and 1.0f; 0.0f being fully
     * transparent and 1.0f fully opaque.</p>
     * @return this renderer's shadow opacity
     */
    public float getOpacity() {
        return opacity;
    }

    /**
     * <p>Sets the opacity used by the renderer to generate shadows.</p>
     * <p>Consecutive calls to {@link #createShadow} will all use this opacity
     * until it is set again.</p>
     * <p>The opacity is comprised between 0.0f and 1.0f; 0.0f being fully
     * transparent and 1.0f fully opaque. If you provide a value out of these
     * boundaries, it will be restrained to the closest boundary.</p>
     * @param shadowOpacity the generated shadows opacity
     */
    public void setOpacity(final float shadowOpacity) {
        float oldOpacity = this.opacity;
        
        if (shadowOpacity < 0.0) {
            this.opacity = 0.0f;
        } else if (shadowOpacity > 1.0f) {
            this.opacity = 1.0f;
        } else {
            this.opacity = shadowOpacity;
        }
        
        changeSupport.firePropertyChange(OPACITY_CHANGED_PROPERTY,
                                         oldOpacity,
                                         this.opacity);
    }

    /**
     * <p>Gets the size in pixel used by the renderer to generate shadows.</p>
     * @return this renderer's shadow size
     */
    public int getSize() {
        return size;
    }

    /**
     * <p>Sets the size, in pixels, used by the renderer to generate shadows.</p>
     * <p>The size defines the blur radius applied to the shadow to create the
     * fuzziness.</p>
     * <p>There is virtually no limit to the size. The size cannot be negative.
     * If you provide a negative value, the size will be 0 instead.</p>
     * @param shadowSize the generated shadows size in pixels (fuzziness)
     */
    public void setSize(final int shadowSize) {
        int oldSize = this.size;
        
        if (shadowSize < 0) {
            this.size = 0;
        } else {
            this.size = shadowSize;
        }
        
        changeSupport.firePropertyChange(SIZE_CHANGED_PROPERTY,
                                         new Integer(oldSize),
                                         new Integer(this.size));
    }

    /**
     * <p>Generates the shadow for a given picture and the current properties
     * of the renderer.</p>
     * <p>The generated image dimensions are computed as following:</p>
     * <pre>
     * width  = imageWidth  + 2 * shadowSize
     * height = imageHeight + 2 * shadowSize
     * </pre>
     * @param image the picture from which the shadow must be cast
     * @return the picture containing the shadow of <code>image</code> 
     */
    public BufferedImage createShadow(final BufferedImage image) {
        int shadowColor = this.color.getRGB();
        int shadowSize = this.size;

        int srcWidth = image.getWidth();
        int srcHeight = image.getHeight();

        int width = srcWidth + 2 * shadowSize;
        int height = image.getHeight() + 2 * shadowSize;

        BufferedImage dst = GraphicsUtilities.createCompatibleImage(image,
                                                                    width,
                                                                    height);

        float shadowOpacity = this.opacity;
        if (shadowOpacity <= 0.0f) {
            return dst;
        }

        int[] tmpPixels = new int[srcWidth * image.getHeight()];
        int[] srcPixels = new int[width * height];
        int[] dstPixels = new int[width * height];

        GraphicsUtilities.getPixels(image, 0, 0, srcWidth, srcHeight, tmpPixels);
        for (int y = 0; y < image.getHeight(); y++) {
            System.arraycopy(tmpPixels, y * srcWidth,
                             srcPixels, (y + shadowSize) * width + shadowSize,
                             srcWidth);
        }

        // horizontal pass
        alphaBlur(srcPixels, dstPixels, width, height,
                  shadowSize, shadowColor, 1.0f);
        // vertical pass
        alphaBlur(dstPixels, srcPixels, height, width,
                  shadowSize, shadowColor, shadowOpacity);
        // the result is now stored in srcPixels due to the 2nd pass
        GraphicsUtilities.setPixels(dst, 0, 0, width, height, srcPixels);

        return dst;
    }

    private static void alphaBlur(int[] srcPixels, int[] dstPixels,
                                  int width, int height, int radius,
                                  int color, float opacity) {
        int windowSize = radius * 2 + 1;
        int radiusPlusOne = radius + 1;

        color &= 0x00FFFFFF;
        int sumAlpha;

        int srcIndex = 0;
        int dstIndex;
        int pixel;

        int[] sumLookupTable = new int[256 * windowSize];
        for (int i = 0; i < sumLookupTable.length; i++) {
            sumLookupTable[i] = i / windowSize;
        }

        int[] indexLookupTable = new int[radiusPlusOne];
        if (radius < width) {
            for (int i = 0; i < indexLookupTable.length; i++) {
                indexLookupTable[i] = i;
            }
        } else {
            for (int i = 0; i < width; i++) {
                indexLookupTable[i] = i;
            }
            for (int i = width; i < indexLookupTable.length; i++) {
                indexLookupTable[i] = width - 1;
            }
        }

        for (int y = 0; y < height; y++) {
            sumAlpha = 0;
            dstIndex = y;

            pixel = srcPixels[srcIndex];
            sumAlpha += (radius + 1) * ((pixel >> 24) & 0xFF);

            for (int i = 1; i <= radius; i++) {
                pixel = srcPixels[srcIndex + indexLookupTable[i]];
                sumAlpha += (pixel >> 24) & 0xFF;
            }

            for  (int x = 0; x < width; x++) {
                dstPixels[dstIndex] =
                        (int) (sumLookupTable[sumAlpha] * opacity) << 24 | color;
                dstIndex += height;

                int nextPixelIndex = x + radiusPlusOne;
                if (nextPixelIndex >= width) {
                    nextPixelIndex = width - 1;
                }

                int previousPixelIndex = x - radius;
                if (previousPixelIndex < 0) {
                    previousPixelIndex = 0;
                }

                int nextPixel = srcPixels[srcIndex + nextPixelIndex];
                int previousPixel = srcPixels[srcIndex + previousPixelIndex];

                sumAlpha += (nextPixel     >> 24) & 0xFF;
                sumAlpha -= (previousPixel >> 24) & 0xFF;
            }

            srcIndex += width;
        }
    }
}
