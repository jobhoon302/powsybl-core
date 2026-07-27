import { NetworkAreaDiagramViewer } from '@powsybl/network-viewer-core';

// The viewer renders an SVG + metadata JSON produced by powsybl-diagram.
// Here we import a sample pair bundled with this starter.
// `?raw` gives us the SVG as a plain string; the JSON is imported as an object.
import svgContent from './data/nad-eurostag-tutorial-example1.svg?raw';
import metadata from './data/nad-eurostag-tutorial-example1_metadata.json';

const container = document.getElementById('nad-container');
if (!container) {
    throw new Error('Missing #nad-container element');
}

new NetworkAreaDiagramViewer(
    container,
    svgContent,
    metadata,
    // NadViewerParametersOptions: enable pan/zoom + toolbar buttons.
    {
        enableDragInteraction: true,
        addButtons: true,
    }
);
