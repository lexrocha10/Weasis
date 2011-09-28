/*******************************************************************************
 * Copyright (c) 2010 Nicolas Roduit.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Nicolas Roduit - initial API and implementation
 ******************************************************************************/
package org.weasis.core.api.image;

import java.awt.geom.Area;
import java.awt.image.RenderedImage;

import javax.media.jai.ROIShape;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weasis.core.api.gui.ImageOperation;
import org.weasis.core.api.gui.util.ActionW;
import org.weasis.core.api.image.op.ShutterDescriptor;
import org.weasis.core.api.media.data.ImageElement;
import org.weasis.core.api.media.data.TagW;

public class ShutterOperation extends AbstractOperation {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShutterOperation.class);

    public static final String name = ActionW.IMAGE_SCHUTTER.getTitle();

    @Override
    public String getOperationName() {
        return name;
    }

    @Override
    public RenderedImage getRenderedImage(RenderedImage source, ImageOperation imageOperation) {
        Boolean shutter = (Boolean) imageOperation.getActionValue(ActionW.IMAGE_SCHUTTER.cmd());
        ImageElement image = imageOperation.getImage();
        Area area = null;
        if (shutter == null || image == null) {
            result = source;
            LOGGER.warn("Cannot apply \"{}\" because a parameter is null", name); //$NON-NLS-1$
        } else if (shutter && (area = (Area) imageOperation.getActionValue(TagW.ShutterFinalShape.getName())) != null) {
            result = ShutterDescriptor.create(source, new ROIShape(area), getShutterColor(imageOperation), null);
        } else {
            result = source;
        }

        return result;
    }

    private int[] getShutterColor(ImageOperation imageOperation) {
        int[] color = (int[]) imageOperation.getActionValue(TagW.ShutterRGBColor.getName());
        if (color != null) {
            // TODO convert LAB to RGB
            // CIELab.getInstance().toRGB(color);
        }

        if (color == null) {
            // A single gray unsigned value used to
            // replace those parts of the image occluded
            // by the shutter, when rendered on a
            // monochrome display. The units are
            // specified in P-Values, from a minimum of
            // 0000H (black) up to a maximum of FFFFH
            // (white).
            Integer val = (Integer) imageOperation.getActionValue(TagW.ShutterPSValue.getName());
            if (val != null) {
                color = new int[] { val >> 8 };
            }
        }
        // color = new int[] { 1300 };
        return color;
    }
}