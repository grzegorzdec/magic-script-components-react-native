import ElementBuilder from './element-builder';

export default class GroupBuilder extends ElementBuilder {
  create(props, id) {
    this._componentManager.createGroupNode(props, id);
  }
}
