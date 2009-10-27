/**
 * 
 */
package org.jdesktop.swingx.color;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.awt.Color;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author Karl George Schaefer
 *
 */
@RunWith(JUnit4.class)
public class ColorUtilTest {
    @Test
    public void testToHexString() {
        assertThat(ColorUtil.toHexString(Color.BLACK), is("#000000"));
        assertThat(ColorUtil.toHexString(Color.WHITE), is("#ffffff"));
    }
    
    @Test
    public void testToHexStringWithTransparentColors() {
        assertThat(ColorUtil.toHexString(ColorUtil.setAlpha(Color.BLACK, 0)), is("#000000"));
        assertThat(ColorUtil.toHexString(ColorUtil.setAlpha(Color.WHITE, 0)), is("#ffffff"));
    }
}
