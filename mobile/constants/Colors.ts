// Color constants
const primaryGreen = "#05C756"; // Bright green for success, accents, wallet balance
const primaryDark = "#00483C"; // Dark teal for headers, primary backgrounds
const accentGreen = "#00D66B"; // Alternative bright green
const errorRed = "#E74C3C"; // Red for errors, negative amounts
const warningOrange = "#FF9500"; // Orange for warnings, bills
const successGreen = "#05C756"; // Success states
const black = "#000000"; // Pure black for buttons, text
const white = "#FFFFFF"; // Pure white for text, backgrounds
const lightGray = "#F5F5F5"; // Light backgrounds
const mediumGray = "#E0E0E0"; // Borders, dividers
const darkGray = "#333333"; // Secondary text
const textGray = "#666666"; // Tertiary text

export default {
  light: {
    // Text colors
    text: black,
    textSecondary: darkGray,
    textTertiary: textGray,
    textInverse: white,

    // Background colors
    background: white,
    backgroundSecondary: lightGray,
    backgroundPrimary: primaryDark,

    // Primary/Tint colors
    tint: primaryGreen,
    tintDark: primaryDark,

    // Tab bar
    tabIconDefault: mediumGray,
    tabIconSelected: primaryGreen,
    tabBackground: white,

    // Accent colors
    success: successGreen,
    error: errorRed,
    warning: warningOrange,

    // UI elements
    border: mediumGray,
    card: white,
    cardHeader: primaryDark,
    buttonPrimary: primaryGreen,
    buttonSecondary: black,
    buttonText: white,
    buttonTextSecondary: white,

    // Status colors
    positive: successGreen,
    negative: errorRed,
  },

  dark: {
    // Text colors
    text: white,
    textSecondary: lightGray,
    textTertiary: mediumGray,
    textInverse: black,

    // Background colors
    background: "#1A1A1A",
    backgroundSecondary: "#2C2C2C",
    backgroundPrimary: primaryDark,

    // Primary/Tint colors
    tint: primaryGreen,
    tintDark: accentGreen,

    // Tab bar
    tabIconDefault: darkGray,
    tabIconSelected: primaryGreen,
    tabBackground: "#1A1A1A",

    // Accent colors
    success: successGreen,
    error: errorRed,
    warning: warningOrange,

    // UI elements
    border: "#444444",
    card: "#2C2C2C",
    cardHeader: primaryDark,
    buttonPrimary: primaryGreen,
    buttonSecondary: "#FFFFFF",
    buttonText: white,
    buttonTextSecondary: black,

    // Status colors
    positive: successGreen,
    negative: errorRed,
  },
};
