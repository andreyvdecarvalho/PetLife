import React, { useState } from 'react';
import './styles.css';

interface BeforeAfterViewerProps {
  beforeUrl: string;
  afterUrl: string;
}

export const BeforeAfterViewer: React.FC<BeforeAfterViewerProps> = ({ beforeUrl, afterUrl }) => {
  const [sliderPosition, setSliderPosition] = useState(50);

  const handleSliderChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSliderPosition(Number(e.target.value));
  };

  return (
    <div className="molecule-before-after-viewer" data-testid="before-after-viewer">
      <div className="molecule-before-after-viewer__container">
        <div className="molecule-before-after-viewer__image-wrapper after">
          <img src={afterUrl} alt="Depois do banho e tosa" className="molecule-before-after-viewer__image" />
        </div>
        <div 
          className="molecule-before-after-viewer__image-wrapper before" 
          style={{ clipPath: `polygon(0 0, ${sliderPosition}% 0, ${sliderPosition}% 100%, 0 100%)` }}
        >
          <img src={beforeUrl} alt="Antes do banho e tosa" className="molecule-before-after-viewer__image" />
        </div>
        
        {/* Slider Line */}
        <div 
          className="molecule-before-after-viewer__slider-line" 
          style={{ left: `${sliderPosition}%` }}
        />

        {/* Slider Handle Button */}
        <div 
          className="molecule-before-after-viewer__slider-button" 
          style={{ left: `${sliderPosition}%` }}
        >
          <span className="material-symbols-outlined">unfold_more</span>
        </div>

        {/* Input Range on top to capture drag */}
        <input
          type="range"
          min="0"
          max="100"
          value={sliderPosition}
          onChange={handleSliderChange}
          className="molecule-before-after-viewer__slider"
          aria-label="Controle de comparação antes e depois"
          data-testid="before-after-slider"
        />

        <div className="molecule-before-after-viewer__label before-label">Antes</div>
        <div className="molecule-before-after-viewer__label after-label">Depois</div>
      </div>
    </div>
  );
};
