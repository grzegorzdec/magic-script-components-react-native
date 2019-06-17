import ElementBuilder from './element-builder';

export class ButtonBuilder extends ElementBuilder {
    create(props, id) {
        this._componentManager.createButtonNode(props, id);
    }
}
