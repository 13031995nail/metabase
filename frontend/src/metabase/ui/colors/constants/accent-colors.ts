/* eslint-disable metabase/no-color-literals -- we define chart colors here to avoid duplication */

import type { ChartColorV2 } from "../types";

import { getBaseColorsForThemeDefinitionOnly } from "./base-colors";

const baseColors = getBaseColorsForThemeDefinitionOnly();

export const DEFAULT_ACCENT_COLORS: ChartColorV2[] = [
  "#0039A6", // accent0 - corporate deep blue
  "#FF6B00", // accent1 - corporate orange
  "#00A3E0", // accent2 - sky blue
  "#0072CE", // accent3 - bright blue
  "#FF9E1B", // accent4 - amber orange
  "#509EE3", // accent5 - soft cyan blue
  "#4A777A", // accent6 - teal
  "#7172AD", // accent7 - indigo
];

export const LIGHT_THEME_ACCENT_COLORS: ChartColorV2[] = [
  ...DEFAULT_ACCENT_COLORS,
  {
    base: baseColors.orion[10],
    tint: baseColors.orion[5],
    shade: baseColors.orion[20],
  },
];

export const DARK_THEME_ACCENT_COLORS: ChartColorV2[] = [
  ...DEFAULT_ACCENT_COLORS,
  {
    base: baseColors.orion[80],
    tint: baseColors.orion[80],
    shade: baseColors.orion[110],
  },
];
