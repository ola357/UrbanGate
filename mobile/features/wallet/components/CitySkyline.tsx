import React from "react";
import Svg, { Rect, Circle, Path } from "react-native-svg";

interface CitySkylineProps {
  width?: number;
  height?: number;
  color?: string;
}

export function CitySkyline({
  width = 200,
  height = 100,
  color = "rgba(255,255,255,0.15)",
}: CitySkylineProps) {
  return (
    <Svg width={width} height={height} viewBox="0 0 200 100">
      {/* Tall building left */}
      <Rect x="10" y="30" width="20" height="70" fill={color} rx="2" />
      <Rect x="13" y="35" width="4" height="4" fill="rgba(255,255,255,0.1)" />
      <Rect x="21" y="35" width="4" height="4" fill="rgba(255,255,255,0.1)" />
      <Rect x="13" y="45" width="4" height="4" fill="rgba(255,255,255,0.1)" />
      <Rect x="21" y="45" width="4" height="4" fill="rgba(255,255,255,0.1)" />

      {/* Short building */}
      <Rect x="35" y="55" width="18" height="45" fill={color} rx="2" />
      <Rect x="38" y="60" width="4" height="4" fill="rgba(255,255,255,0.1)" />
      <Rect x="46" y="60" width="4" height="4" fill="rgba(255,255,255,0.1)" />

      {/* Tree */}
      <Circle cx="65" cy="72" r="10" fill={color} />
      <Rect x="63" y="82" width="4" height="18" fill={color} />

      {/* Dome / mosque */}
      <Path
        d="M85 100 L85 60 Q95 40 105 60 L105 100 Z"
        fill={color}
      />
      <Rect x="93" y="35" width="2" height="10" fill={color} />
      <Circle cx="94" cy="33" r="3" fill={color} />

      {/* Medium building */}
      <Rect x="115" y="45" width="22" height="55" fill={color} rx="2" />
      <Rect x="119" y="50" width="4" height="4" fill="rgba(255,255,255,0.1)" />
      <Rect x="129" y="50" width="4" height="4" fill="rgba(255,255,255,0.1)" />
      <Rect x="119" y="60" width="4" height="4" fill="rgba(255,255,255,0.1)" />
      <Rect x="129" y="60" width="4" height="4" fill="rgba(255,255,255,0.1)" />

      {/* Tree right */}
      <Circle cx="150" cy="78" r="8" fill={color} />
      <Rect x="148" y="86" width="4" height="14" fill={color} />

      {/* Tall tower right */}
      <Rect x="165" y="20" width="16" height="80" fill={color} rx="2" />
      <Rect x="168" y="25" width="3" height="3" fill="rgba(255,255,255,0.1)" />
      <Rect x="175" y="25" width="3" height="3" fill="rgba(255,255,255,0.1)" />
      <Rect x="168" y="35" width="3" height="3" fill="rgba(255,255,255,0.1)" />
      <Rect x="175" y="35" width="3" height="3" fill="rgba(255,255,255,0.1)" />
      <Rect x="168" y="45" width="3" height="3" fill="rgba(255,255,255,0.1)" />
      <Rect x="175" y="45" width="3" height="3" fill="rgba(255,255,255,0.1)" />

      {/* Small building far right */}
      <Rect x="186" y="60" width="14" height="40" fill={color} rx="2" />
    </Svg>
  );
}
