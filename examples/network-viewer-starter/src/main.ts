import { NetworkAreaDiagramViewer } from '@powsybl/network-viewer-core';

// The viewer only displays an SVG + its metadata JSON, both produced by
// powsybl-diagram (or the powsybl-core NetworkAreaDiagram API).
// The sample pair lives in `public/data/`, so we load it at runtime with fetch:
// no bundler-specific import suffix (like `?raw`) and no extra type declarations
// are required.
async function renderDiagram(): Promise<void> {
    const container = document.getElementById('nad-container');
    if (!container) {
        throw new Error('Missing #nad-container element');
    }

    const [svgContent, metadata] = await Promise.all([
        fetch('data/nad.svg').then((response) => response.text()),
        fetch('data/nad_metadata.json').then((response) => response.json()),
    ]);

    new NetworkAreaDiagramViewer(container, svgContent, metadata, {
        enableDragInteraction: true,
        addButtons: true,
    });
}

renderDiagram();
