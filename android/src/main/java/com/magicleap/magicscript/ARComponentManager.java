/*
 *  Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.magicleap.magicscript;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.google.ar.sceneform.Node;
import com.magicleap.magicscript.ar.CubeRenderableBuilder;
import com.magicleap.magicscript.ar.CubeRenderableBuilderImpl;
import com.magicleap.magicscript.ar.ModelRenderableLoader;
import com.magicleap.magicscript.ar.ModelRenderableLoaderImpl;
import com.magicleap.magicscript.ar.VideoRenderableLoader;
import com.magicleap.magicscript.ar.VideoRenderableLoaderImpl;
import com.magicleap.magicscript.ar.ViewRenderableLoader;
import com.magicleap.magicscript.ar.ViewRenderableLoaderImpl;
import com.magicleap.magicscript.font.FontProvider;
import com.magicleap.magicscript.font.providers.AndroidFontProvider;
import com.magicleap.magicscript.font.providers.FontProviderImpl;
import com.magicleap.magicscript.icons.DefaultIconsProvider;
import com.magicleap.magicscript.icons.ExternalIconsProvider;
import com.magicleap.magicscript.icons.IconsRepository;
import com.magicleap.magicscript.icons.IconsRepositoryImpl;
import com.magicleap.magicscript.icons.ToggleIconsProviderImpl;
import com.magicleap.magicscript.scene.UiNodesManager;
import com.magicleap.magicscript.scene.nodes.DialogNode;
import com.magicleap.magicscript.scene.nodes.GroupNode;
import com.magicleap.magicscript.scene.nodes.LineNode;
import com.magicleap.magicscript.scene.nodes.ModelNode;
import com.magicleap.magicscript.scene.nodes.PanelNode;
import com.magicleap.magicscript.scene.nodes.UIWebViewNode;
import com.magicleap.magicscript.scene.nodes.UiButtonNode;
import com.magicleap.magicscript.scene.nodes.UiCircleConfirmationNode;
import com.magicleap.magicscript.scene.nodes.UiColorPickerNode;
import com.magicleap.magicscript.scene.nodes.UiDatePickerNode;
import com.magicleap.magicscript.scene.nodes.UiDropdownListItemNode;
import com.magicleap.magicscript.scene.nodes.UiDropdownListNode;
import com.magicleap.magicscript.scene.nodes.UiImageNode;
import com.magicleap.magicscript.scene.nodes.UiListViewItemNode;
import com.magicleap.magicscript.scene.nodes.UiListViewNode;
import com.magicleap.magicscript.scene.nodes.UiProgressBarNode;
import com.magicleap.magicscript.scene.nodes.UiScrollBarNode;
import com.magicleap.magicscript.scene.nodes.UiScrollViewNode;
import com.magicleap.magicscript.scene.nodes.UiSliderNode;
import com.magicleap.magicscript.scene.nodes.UiSpinnerNode;
import com.magicleap.magicscript.scene.nodes.UiTabNode;
import com.magicleap.magicscript.scene.nodes.UiTextEditNode;
import com.magicleap.magicscript.scene.nodes.UiTextNode;
import com.magicleap.magicscript.scene.nodes.UiTimePickerNode;
import com.magicleap.magicscript.scene.nodes.audio.AudioNode;
import com.magicleap.magicscript.scene.nodes.base.TransformNode;
import com.magicleap.magicscript.scene.nodes.base.UiNode;
import com.magicleap.magicscript.scene.nodes.layouts.UiGridLayout;
import com.magicleap.magicscript.scene.nodes.layouts.UiLinearLayout;
import com.magicleap.magicscript.scene.nodes.layouts.UiRectLayout;
import com.magicleap.magicscript.scene.nodes.layouts.manager.GridLayoutManager;
import com.magicleap.magicscript.scene.nodes.layouts.manager.GridLayoutManagerImpl;
import com.magicleap.magicscript.scene.nodes.layouts.manager.RectLayoutManager;
import com.magicleap.magicscript.scene.nodes.layouts.manager.RectLayoutManagerImpl;
import com.magicleap.magicscript.scene.nodes.toggle.LinearToggleViewManager;
import com.magicleap.magicscript.scene.nodes.toggle.ToggleGroupNode;
import com.magicleap.magicscript.scene.nodes.toggle.ToggleViewManager;
import com.magicleap.magicscript.scene.nodes.toggle.UiToggleNode;
import com.magicleap.magicscript.scene.nodes.video.MediaPlayerPool;
import com.magicleap.magicscript.scene.nodes.video.VideoNode;
import com.magicleap.magicscript.scene.nodes.video.VideoPlayer;
import com.magicleap.magicscript.scene.nodes.video.VideoPlayerImpl;
import com.magicleap.magicscript.scene.nodes.views.DialogProviderImpl;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;

import kotlin.Unit;

/**
 * A React module that is responsible for "parsing" JS tags in order to generate AR Nodes
 * Based on: https://facebook.github.io/react-native/docs/native-modules-android
 * <p>
 * Node creation methods are called from
 * react-native-magic-script/components/platform/platform-factory.js
 */
@SuppressWarnings("unused")
public class ARComponentManager extends ReactContextBaseJavaModule implements LifecycleEventListener {

    private static final String COMPONENT_NAME = "ARComponentManager";

    // Supported events names
    private static final String EVENT_CLICK = "onClick";
    private static final String EVENT_PRESS = "onPress";
    private static final String EVENT_TEXT_CHANGED = "onTextChanged";
    private static final String EVENT_TOGGLE_CHANGED = "onToggleChanged";
    private static final String EVENT_VIDEO_PREPARED = "onVideoPrepared";
    private static final String EVENT_DROPDOWN_SELECTION_CHANGED = "onSelectionChanged";
    private static final String EVENT_SLIDER_VALUE_CHANGED = "onSliderChanged";
    private static final String EVENT_COLOR_CONFIRMED = "onColorConfirmed";
    private static final String EVENT_COLOR_CANCELLED = "onColorCanceled";
    private static final String EVENT_COLOR_CHANGED = "onColorChanged";
    private static final String EVENT_DATE_CHANGED = "onDateChanged";
    private static final String EVENT_DATE_CONFIRMED = "onDateConfirmed";
    private static final String EVENT_TIME_CHANGED = "onTimeChanged";
    private static final String EVENT_TIME_CONFIRMED = "onTimeConfirmed";
    private static final String EVENT_SCROLL_CHANGED = "onScrollChanged";
    private static final String EVENT_DIALOG_CONFIRMED = "onDialogConfirmed";
    private static final String EVENT_DIALOG_CANCELED = "onDialogCanceled";
    private static final String EVENT_DIALOG_EXPIRED = "onDialogTimeExpired";
    private static final String EVENT_CONFIRMATION_COMPLETED = "onConfirmationCompleted";
    private static final String EVENT_CONFIRMATION_UPDATED = "onConfirmationUpdated";
    private static final String EVENT_CONFIRMATION_CANCELED = "onConfirmationCanceled";

    // Supported events arguments
    private static final String EVENT_ARG_NODE_ID = "nodeId";
    private static final String EVENT_ARG_TEXT = "text";
    private static final String EVENT_ARG_TOGGLE_ACTIVE = "On";
    private static final String EVENT_ARG_SELECTED_ITEMS = "selectedItemsIndexes";
    private static final String EVENT_ARG_SLIDER_VALUE = "Value";
    private static final String EVENT_ARG_COLOR = "color";
    private static final String EVENT_ARG_DATE = "date";
    private static final String EVENT_ARG_TIME = "time";
    private static final String EVENT_ARG_SCROLL_VALUE = "ScrollValue";
    private static final String EVENT_ARG_CONFIRMATION_UPDATED_VALUE = "Angle";

    // All code inside react method must be called from main thread
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private ReactApplicationContext context;

    // Renderable loaders
    private ViewRenderableLoader viewRenderableLoader;
    private ModelRenderableLoader modelRenderableLoader;
    private VideoRenderableLoader videoRenderableLoader;
    private CubeRenderableBuilder cubeRenderableBuilder;

    // Other resources providers
    private FontProvider fontProvider;
    private IconsRepository iconsRepo;

    public ARComponentManager(ReactApplicationContext reactContext) {
        super(reactContext);
        this.context = reactContext;
        this.viewRenderableLoader = new ViewRenderableLoaderImpl(context);
        this.modelRenderableLoader = new ModelRenderableLoaderImpl(context);
        this.videoRenderableLoader = new VideoRenderableLoaderImpl(context);
        this.cubeRenderableBuilder = new CubeRenderableBuilderImpl(context);

        AndroidFontProvider androidFontProvider = new AndroidFontProvider();
        this.fontProvider = new FontProviderImpl(context, androidFontProvider);

        DefaultIconsProvider defaultIconsProvider = new DefaultIconsProvider(context);
        ExternalIconsProvider externalIconsProvider = new ExternalIconsProvider(context);
        this.iconsRepo = new IconsRepositoryImpl(defaultIconsProvider, externalIconsProvider);

        context.addLifecycleEventListener(this);
    }

    @NotNull
    @Override
    public String getName() {
        return COMPONENT_NAME;
    }

    @Override
    public Map<String, Object> getConstants() {
        return Collections.emptyMap();
    }

    /**
     * Creates node that is a parent for other nodes (<view>)
     *
     * @param props  properties (e.g. localPosition)
     * @param nodeId id of the node
     */
    @ReactMethod
    public void createGroupNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new GroupNode(props), nodeId));
    }

    /**
     * Creates a button
     *
     * @param props  properties (e.g. localPosition)
     * @param nodeId id of the node
     */
    @ReactMethod
    public void createButtonNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> {
            UiButtonNode node
                    = new UiButtonNode(props, context, viewRenderableLoader, fontProvider, iconsRepo);
            addNode(node, nodeId);
        });
    }

    @ReactMethod
    public void createImageNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> {
            UiImageNode node = new UiImageNode(props, context, viewRenderableLoader, iconsRepo);
            addNode(node, nodeId);
        });
    }

    @ReactMethod
    public void createTextNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> {
            UiTextNode node = new UiTextNode(props, context, viewRenderableLoader, fontProvider);
            addNode(node, nodeId);
        });
    }

    @ReactMethod
    public void createTextEditNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> {
            UiTextEditNode node = new UiTextEditNode(props, context, viewRenderableLoader, fontProvider);
            addNode(node, nodeId);
        });
    }

    @ReactMethod
    public void createModelNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new ModelNode(props, context, modelRenderableLoader), nodeId));
    }

    @ReactMethod
    public void createVideoNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> {
            VideoPlayer videoPlayer = new VideoPlayerImpl(context);
            addNode(new VideoNode(props, context, videoPlayer, videoRenderableLoader), nodeId);
        });
    }

    @ReactMethod
    public void createScrollBarNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new UiScrollBarNode(props), nodeId));
    }

    @ReactMethod
    public void createScrollViewNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new UiScrollViewNode(props, context, viewRenderableLoader), nodeId));
    }

    @ReactMethod
    public void createSliderNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new UiSliderNode(props, context, viewRenderableLoader), nodeId));
    }

    @ReactMethod
    public void createSpinnerNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new UiSpinnerNode(props, context, viewRenderableLoader), nodeId));
    }

    @ReactMethod
    public void createCircleConfirmationNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new UiCircleConfirmationNode(props, context, viewRenderableLoader), nodeId));
    }

    @ReactMethod
    public void createToggleNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> {
            ToggleViewManager manager = new LinearToggleViewManager(fontProvider, new ToggleIconsProviderImpl());
            UiToggleNode node = new UiToggleNode(props, context, viewRenderableLoader, manager);
            addNode(node, nodeId);
        });
    }

    @ReactMethod
    public void createToggleGroupNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new ToggleGroupNode(props), nodeId));
    }

    @ReactMethod
    public void createProgressBarNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new UiProgressBarNode(props, context, viewRenderableLoader), nodeId));
    }

    @ReactMethod
    public void createLineNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new LineNode(props, context, cubeRenderableBuilder), nodeId));
    }

    @ReactMethod
    public void createGridLayoutNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> {
            GridLayoutManager layoutManager = new GridLayoutManagerImpl();
            addNode(new UiGridLayout(props, layoutManager), nodeId);
        });
    }

    @ReactMethod
    public void createLinearLayoutNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new UiLinearLayout(props), nodeId));
    }


    @ReactMethod
    public void createDropdownListNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> {
            UiDropdownListNode node
                    = new UiDropdownListNode(props, context, viewRenderableLoader, fontProvider, iconsRepo);
            addNode(node, nodeId);
        });
    }

    @ReactMethod
    public void createDropdownListItemNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> {
            UiDropdownListItemNode node = new UiDropdownListItemNode(props, context, viewRenderableLoader, fontProvider);
            addNode(node, nodeId);
        });
    }

    @ReactMethod
    public void createColorPickerNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> {
            UiColorPickerNode node = new UiColorPickerNode(props, context, viewRenderableLoader, fontProvider, iconsRepo);
            addNode(node, nodeId);
        });
    }

    @ReactMethod
    public void createWebViewNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> {
            UIWebViewNode node = new UIWebViewNode(props, context, viewRenderableLoader);
            addNode(node, nodeId);
        });
    }

    @ReactMethod
    public void createRectLayoutNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> {
            RectLayoutManager layoutManager = new RectLayoutManagerImpl();
            addNode(new UiRectLayout(props, layoutManager), nodeId);
        });
    }

    @ReactMethod
    public void createListViewItemNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new UiListViewItemNode(props, context, viewRenderableLoader), nodeId));
    }

    @ReactMethod
    public void createListViewNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new UiListViewNode(props, context, viewRenderableLoader), nodeId));
    }

    @ReactMethod
    public void createDatePickerNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> {
            UiDatePickerNode datePickerNode = new UiDatePickerNode(props, context, viewRenderableLoader, new DialogProviderImpl());
            addNode(datePickerNode, nodeId);
        });
    }

    @ReactMethod
    public void createTimePickerNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> {
            UiTimePickerNode datePickerNode = new UiTimePickerNode(props, context, viewRenderableLoader, new DialogProviderImpl());
            addNode(datePickerNode, nodeId);
        });
    }

    @ReactMethod
    public void createDialogNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> {
            DialogNode node = new DialogNode(props, ArViewManager.getActivityRef().get(), iconsRepo, new DialogProviderImpl());
            addNode(node, nodeId);
        });
    }

    @ReactMethod
    public void createTabNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> {
            UiTabNode node = new UiTabNode(props, context, viewRenderableLoader, fontProvider, iconsRepo);
            addNode(node, nodeId);
        });
    }

    @ReactMethod
    public void createPanelNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> {
            addNode(new PanelNode(props), nodeId);
        });
    }

    @ReactMethod
    public void createAudioNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> {
            AudioNode node = new AudioNode(props, context);
            addNode(node, nodeId);
        });
    }

    @ReactMethod
    public void addChildNode(final String nodeId, final String parentId) {
        mainHandler.post(() -> UiNodesManager.addNodeToParent(nodeId, parentId));
    }

    @ReactMethod
    public void addChildNodeToContainer(final String nodeId) {
        mainHandler.post(() -> UiNodesManager.addNodeToRoot(nodeId));
    }

    @ReactMethod
    public void removeChildNode(final String nodeId, final String parentId) {
        mainHandler.post(() -> UiNodesManager.removeNode(nodeId));
    }

    @ReactMethod
    public void removeChildNodeFromRoot(final String nodeId) {
        mainHandler.post(() -> UiNodesManager.removeNode(nodeId));
    }

    @ReactMethod
    public void updateNode(final String nodeId, final ReadableMap properties) {
        mainHandler.post(() -> UiNodesManager.updateNode(nodeId, properties));
    }

    @ReactMethod
    public void clearScene() {
        mainHandler.post(() -> UiNodesManager.clear());
    }

    @ReactMethod
    public void addOnPressEventHandler(final String nodeId) {
        mainHandler.post(() -> {
            final Node node = UiNodesManager.findNodeWithId(nodeId);
            if (node instanceof UiNode) {
                ((UiNode) node).setClickListener(() -> {
                    WritableMap pressParams = Arguments.createMap();
                    pressParams.putString(EVENT_ARG_NODE_ID, nodeId);

                    // must use separate map
                    WritableMap clickParams = Arguments.createMap();
                    clickParams.putString(EVENT_ARG_NODE_ID, nodeId);

                    sendEvent(EVENT_PRESS, pressParams);
                    sendEvent(EVENT_CLICK, clickParams);
                    return Unit.INSTANCE;
                });
            }
        });
    }

    @ReactMethod
    public void removeOnPressEventHandler(final String nodeId) {
        mainHandler.post(() -> {
            Node node = UiNodesManager.findNodeWithId(nodeId);
            if (node instanceof UiNode) {
                ((UiNode) node).setClickListener(null);
            }
        });
    }

    @ReactMethod
    public void addOnTextChangedEventHandler(final String nodeId) {
        mainHandler.post(() -> {
            final Node node = UiNodesManager.findNodeWithId(nodeId);
            if (node instanceof UiTextEditNode) {
                ((UiTextEditNode) node).setTextChangedListener(text -> {
                    WritableMap params = Arguments.createMap();
                    params.putString(EVENT_ARG_NODE_ID, nodeId);
                    params.putString(EVENT_ARG_TEXT, text);

                    sendEvent(EVENT_TEXT_CHANGED, params);
                    return Unit.INSTANCE;
                });
            }
        });
    }

    @ReactMethod
    public void addOnToggleChangedEventHandler(final String nodeId) {
        mainHandler.post(() -> {
            final Node node = UiNodesManager.findNodeWithId(nodeId);
            if (node instanceof UiToggleNode) {
                ((UiToggleNode) node).setToggleChangedListener(isOn -> {
                    WritableMap params = Arguments.createMap();
                    params.putString(EVENT_ARG_NODE_ID, nodeId);
                    params.putBoolean(EVENT_ARG_TOGGLE_ACTIVE, isOn);

                    sendEvent(EVENT_TOGGLE_CHANGED, params);
                    return Unit.INSTANCE;
                });
            }
        });
    }

    @ReactMethod
    public void addOnVideoPreparedEventHandler(final String nodeId) {
        mainHandler.post(() -> {
            final Node node = UiNodesManager.findNodeWithId(nodeId);
            if (node instanceof VideoNode) {
                ((VideoNode) node).setOnVideoPreparedListener(() -> {
                    WritableMap params = Arguments.createMap();
                    params.putString(EVENT_ARG_NODE_ID, nodeId);
                    sendEvent(EVENT_VIDEO_PREPARED, params);
                    return Unit.INSTANCE;
                });
            }
        });
    }

    @ReactMethod
    public void addOnSliderChangedEventHandler(final String nodeId) {
        mainHandler.post(() -> {
            final Node node = UiNodesManager.findNodeWithId(nodeId);
            if (node instanceof UiSliderNode) {
                ((UiSliderNode) node).setOnSliderChangedListener((value) -> {
                    WritableMap params = Arguments.createMap();
                    params.putString(EVENT_ARG_NODE_ID, nodeId);
                    params.putDouble(EVENT_ARG_SLIDER_VALUE, value);
                    sendEvent(EVENT_SLIDER_VALUE_CHANGED, params);
                    return Unit.INSTANCE;
                });
            }
        });
    }

    @ReactMethod
    public void addOnSelectionChangedEventHandler(final String nodeId) {
        mainHandler.post(() -> {
            final Node node = UiNodesManager.findNodeWithId(nodeId);
            if (node instanceof UiDropdownListNode) {
                ((UiDropdownListNode) node).setOnSelectionChangedListener((itemIndex) -> {
                    WritableMap params = Arguments.createMap();
                    params.putString(EVENT_ARG_NODE_ID, nodeId);
                    WritableArray selectedItems = Arguments.createArray();
                    selectedItems.pushInt(itemIndex);
                    params.putArray(EVENT_ARG_SELECTED_ITEMS, selectedItems);
                    sendEvent(EVENT_DROPDOWN_SELECTION_CHANGED, params);
                    return Unit.INSTANCE;
                });
            }
        });
    }

    @ReactMethod
    public void addOnColorConfirmedEventHandler(final String nodeId) {
        mainHandler.post(() -> {
            final Node node = UiNodesManager.findNodeWithId(nodeId);
            if (node instanceof UiColorPickerNode) {
                ((UiColorPickerNode) node).setOnColorConfirmed((colors) -> {
                    WritableMap params = Arguments.createMap();
                    params.putString(EVENT_ARG_NODE_ID, nodeId);
                    WritableArray selectedItems = Arguments.createArray();
                    for (final Double color : colors) {
                        selectedItems.pushDouble(color);
                    }
                    params.putArray(EVENT_ARG_COLOR, selectedItems);
                    sendEvent(EVENT_COLOR_CONFIRMED, params);
                    return Unit.INSTANCE;
                });
            }
        });
    }

    @ReactMethod
    public void addOnColorCanceledEventHandler(final String nodeId) {
        mainHandler.post(() -> {
            final Node node = UiNodesManager.findNodeWithId(nodeId);
            if (node instanceof UiColorPickerNode) {
                ((UiColorPickerNode) node).setOnColorCanceled(() -> {
                    WritableMap params = Arguments.createMap();
                    params.putString(EVENT_ARG_NODE_ID, nodeId);
                    sendEvent(EVENT_COLOR_CANCELLED, params);
                    return Unit.INSTANCE;
                });
            }
        });
    }

    @ReactMethod
    public void addOnColorChangedEventHandler(final String nodeId) {
        mainHandler.post(() -> {
            final Node node = UiNodesManager.findNodeWithId(nodeId);
            if (node instanceof UiColorPickerNode) {
                ((UiColorPickerNode) node).setOnColorConfirmed((colors) -> {
                    WritableMap params = Arguments.createMap();
                    params.putString(EVENT_ARG_NODE_ID, nodeId);
                    WritableArray selectedItems = Arguments.createArray();
                    for (final Double color : colors) {
                        selectedItems.pushDouble(color);
                    }
                    params.putArray(EVENT_ARG_COLOR, selectedItems);
                    sendEvent(EVENT_COLOR_CHANGED, params);
                    return Unit.INSTANCE;
                });
            }
        });
    }

    @ReactMethod
    public void addOnDateChangedEventHandler(final String nodeId) {
        mainHandler.post(() -> {
            final Node node = UiNodesManager.findNodeWithId(nodeId);
            if (node instanceof UiDatePickerNode) {
                ((UiDatePickerNode) node).setOnDateChanged((date) -> {
                    WritableMap params = Arguments.createMap();
                    params.putString(EVENT_ARG_NODE_ID, nodeId);
                    params.putString(EVENT_ARG_DATE, date);
                    sendEvent(EVENT_DATE_CHANGED, params);
                    return Unit.INSTANCE;
                });
            }
        });
    }


    @ReactMethod
    public void addOnDateConfirmedEventHandler(final String nodeId) {
        mainHandler.post(() -> {
            final Node node = UiNodesManager.findNodeWithId(nodeId);
            if (node instanceof UiDatePickerNode) {
                ((UiDatePickerNode) node).setOnDateConfirmed((date) -> {
                    WritableMap params = Arguments.createMap();
                    params.putString(EVENT_ARG_NODE_ID, nodeId);
                    params.putString(EVENT_ARG_DATE, date);
                    sendEvent(EVENT_DATE_CONFIRMED, params);
                    return Unit.INSTANCE;
                });
            }
        });
    }

    @ReactMethod
    public void addOnScrollChangedEventHandler(final String nodeId) {
        mainHandler.post(() -> {
            final Node node = UiNodesManager.findNodeWithId(nodeId);
            if (node instanceof UiScrollViewNode) {
                ((UiScrollViewNode) node).setOnScrollChangeListener((position) -> {
                    WritableMap params = Arguments.createMap();
                    params.putString(EVENT_ARG_NODE_ID, nodeId);
                    params.putDouble(EVENT_ARG_SCROLL_VALUE, position.getX());
                    sendEvent(EVENT_SCROLL_CHANGED, params);
                    return Unit.INSTANCE;
                });
            }
        });
    }


    @ReactMethod
    public void addOnTimeChangedEventHandler(final String nodeId) {
        mainHandler.post(() -> {
            final Node node = UiNodesManager.findNodeWithId(nodeId);
            if (node instanceof UiTimePickerNode) {
                ((UiTimePickerNode) node).setOnTimeChanged((time) -> {
                    WritableMap params = Arguments.createMap();
                    params.putString(EVENT_ARG_NODE_ID, nodeId);
                    params.putString(EVENT_ARG_TIME, time);
                    sendEvent(EVENT_TEXT_CHANGED, params);
                    return Unit.INSTANCE;
                });
            }
        });
    }


    @ReactMethod
    public void addOnTimeConfirmedEventHandler(final String nodeId) {
        mainHandler.post(() -> {
            final Node node = UiNodesManager.findNodeWithId(nodeId);
            if (node instanceof UiTimePickerNode) {
                ((UiTimePickerNode) node).setOnTimeConfirmed((time) -> {
                    WritableMap params = Arguments.createMap();
                    params.putString(EVENT_ARG_NODE_ID, nodeId);
                    params.putString(EVENT_ARG_TIME, time);
                    sendEvent(EVENT_TIME_CONFIRMED, params);
                    return Unit.INSTANCE;
                });
            }
        });
    }

    @ReactMethod
    public void addOnDialogConfirmedEventHandler(final String nodeId) {
        mainHandler.post(() -> {
            final Node node = UiNodesManager.findNodeWithId(nodeId);
            if (node instanceof DialogNode) {
                ((DialogNode) node).setOnDialogConfirmListener(() -> {
                    WritableMap params = Arguments.createMap();
                    params.putString(EVENT_ARG_NODE_ID, nodeId);
                    sendEvent(EVENT_DIALOG_CONFIRMED, params);
                    return Unit.INSTANCE;
                });
            }
        });
    }

    @ReactMethod
    public void addOnDialogCanceledEventHandler(final String nodeId) {
        mainHandler.post(() -> {
            final Node node = UiNodesManager.findNodeWithId(nodeId);
            if (node instanceof DialogNode) {
                ((DialogNode) node).setOnDialogCancelListener(() -> {
                    WritableMap params = Arguments.createMap();
                    params.putString(EVENT_ARG_NODE_ID, nodeId);
                    sendEvent(EVENT_DIALOG_CANCELED, params);
                    return Unit.INSTANCE;
                });
            }
        });
    }

    @ReactMethod
    public void addOnDialogTimeExpiredEventHandler(final String nodeId) {
        mainHandler.post(() -> {
            final Node node = UiNodesManager.findNodeWithId(nodeId);
            if (node instanceof DialogNode) {
                ((DialogNode) node).setOnDialogExpiredListener(() -> {
                    WritableMap params = Arguments.createMap();
                    params.putString(EVENT_ARG_NODE_ID, nodeId);
                    sendEvent(EVENT_DIALOG_EXPIRED, params);
                    return Unit.INSTANCE;
                });
            }
        });
    }

    @ReactMethod
    public void addOnConfirmationCompletedEventHandler(final String nodeId) {
        mainHandler.post(() -> {
            final Node node = UiNodesManager.findNodeWithId(nodeId);
            if (node instanceof UiCircleConfirmationNode) {
                ((UiCircleConfirmationNode) node).setOnConfirmationCompletedListener(() -> {
                    WritableMap params = Arguments.createMap();
                    params.putString(EVENT_ARG_NODE_ID, nodeId);
                    sendEvent(EVENT_CONFIRMATION_COMPLETED, params);
                    return Unit.INSTANCE;
                });
            }
        });
    }

    @ReactMethod
    public void addOnConfirmationUpdatedEventHandler(final String nodeId) {
        mainHandler.post(() -> {
            final Node node = UiNodesManager.findNodeWithId(nodeId);
            if (node instanceof UiCircleConfirmationNode) {
                ((UiCircleConfirmationNode) node).setOnConfirmationUpdatedListener((value) -> {
                    WritableMap params = Arguments.createMap();
                    params.putString(EVENT_ARG_NODE_ID, nodeId);
                    params.putDouble(EVENT_ARG_CONFIRMATION_UPDATED_VALUE, value);
                    sendEvent(EVENT_CONFIRMATION_UPDATED, params);
                    return Unit.INSTANCE;
                });
            }
        });
    }

    @ReactMethod
    public void addOnConfirmationCanceledEventHandler(final String nodeId) {
        mainHandler.post(() -> {
            final Node node = UiNodesManager.findNodeWithId(nodeId);
            if (node instanceof UiCircleConfirmationNode) {
                ((UiCircleConfirmationNode) node).setOnConfirmationCanceledListener(() -> {
                    WritableMap params = Arguments.createMap();
                    params.putString(EVENT_ARG_NODE_ID, nodeId);
                    sendEvent(EVENT_CONFIRMATION_CANCELED, params);
                    return Unit.INSTANCE;
                });
            }
        });
    }

    @ReactMethod
    public void updateLayout() {
        // unused on Android
    }

    private void addNode(TransformNode node, String nodeId) {
        node.build();
        UiNodesManager.registerNode(node, nodeId);
    }

    private void sendEvent(String eventName, @Nullable WritableMap params) {
        context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
    }

    @Override
    public void onHostResume() {

    }

    @Override
    public void onHostPause() {

    }

    @Override
    public void onHostDestroy() {
        MediaPlayerPool.INSTANCE.destroy();
    }
}