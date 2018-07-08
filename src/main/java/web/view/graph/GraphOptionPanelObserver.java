package web.view.graph;

/**
 * An observer that is meant to observe changes in the GraphOptionPanel.
 */
public interface GraphOptionPanelObserver {

    /**
     * Update the observer  with the current GraphOptionPanel state.
     */
    public void graphOptionUpdate();

}