// Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved

import ButtonBuilder from './elements/builders/button-builder.js';
import GridLayoutBuilder from './elements/builders/grid-layout-builder.js';
import GroupBuilder from './elements/builders/group-builder.js';
import ImageBuilder from './elements/builders/image-builder.js';
import LineBuilder from './elements/builders/line-builder.js';
import LinearLayoutBuilder from './elements/builders/linear-layout-builder.js';
import ModelBuilder from './elements/builders/model-builder.js';
import ProgressBarBuilder from './elements/builders/progress-bar-builder.js';
import ScrollBarBuilder from './elements/builders/scroll-bar-builder.js';
import SliderBuilder from './elements/builders/slider-builder.js';
import SpinnerBuilder from './elements/builders/spinner-builder.js';
import TextBuilder from './elements/builders/text-builder.js';
import TextEditBuilder from './elements/builders/text-edit-builder.js';
import ToggleBuilder from './elements/builders/toggle-builder.js';
import VideoBuilder from './elements/builders/video-builder.js';


export default {
    version: '1.0',
    platform: 'mobile (ios, android)',
    controllers: {
        // 'scene': () => new ControllerBuilder(),
    },
    elements: {
        // ui nodes
        'button': (componentManager) => new ButtonBuilder(componentManager),
        'gridLayout': (componentManager) => new GridLayoutBuilder(componentManager),
        'image': (componentManager) => new ImageBuilder(componentManager),
        'linearLayout': (componentManager) => new LinearLayoutBuilder(componentManager),
        'text': (componentManager) => new TextBuilder(componentManager),
        'textEdit': (componentManager) => new TextEditBuilder(componentManager),
        'toggle': (componentManager) => new ToggleBuilder(componentManager),
        'progressBar': (componentManager) => new ProgressBarBuilder(componentManager),
        'scrollBar': (componentManager) => new ScrollBarBuilder(componentManager),
        'slider': (componentManager) => new SliderBuilder(componentManager),
        'spinner': (componentManager) => new SpinnerBuilder(componentManager),
        'view': (componentManager) => new GroupBuilder(componentManager),

        // render nodes
        'line': (componentManager) => new LineBuilder(componentManager),
        'model': (componentManager) => new ModelBuilder(componentManager),
        'video': (componentManager) => new VideoBuilder(componentManager),
    }
};
