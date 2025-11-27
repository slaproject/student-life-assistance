'use client';
import { createTheme } from '@mui/material/styles';

// Create a custom theme with our typography
const theme = createTheme({
    typography: {
        fontFamily: 'var(--font-body)',
        h1: {
            fontFamily: 'var(--font-heading)',
            fontWeight: 800,
            fontSize: '3rem',
            letterSpacing: '-0.02em',
            lineHeight: 1.2,
        },
        h2: {
            fontFamily: 'var(--font-heading)',
            fontWeight: 700,
            fontSize: '2.25rem',
            letterSpacing: '-0.02em',
            lineHeight: 1.2,
        },
        h3: {
            fontFamily: 'var(--font-heading)',
            fontWeight: 600,
            fontSize: '1.875rem',
            letterSpacing: '-0.02em',
            lineHeight: 1.2,
        },
        h4: {
            fontFamily: 'var(--font-heading)',
            fontWeight: 600,
            fontSize: '1.5rem',
            letterSpacing: '-0.02em',
            lineHeight: 1.2,
        },
        h5: {
            fontFamily: 'var(--font-heading)',
            fontWeight: 500,
            fontSize: '1.25rem',
            letterSpacing: '-0.02em',
            lineHeight: 1.2,
        },
        h6: {
            fontFamily: 'var(--font-heading)',
            fontWeight: 500,
            fontSize: '1rem',
            letterSpacing: '-0.02em',
            lineHeight: 1.2,
        },
        subtitle1: {
            fontFamily: 'var(--font-body)',
            fontWeight: 500,
            fontSize: '1rem',
            lineHeight: 1.6,
        },
        subtitle2: {
            fontFamily: 'var(--font-body)',
            fontWeight: 500,
            fontSize: '0.875rem',
            lineHeight: 1.6,
        },
        body1: {
            fontFamily: 'var(--font-body)',
            fontWeight: 400,
            fontSize: '1rem',
            lineHeight: 1.6,
        },
        body2: {
            fontFamily: 'var(--font-body)',
            fontWeight: 400,
            fontSize: '0.875rem',
            lineHeight: 1.6,
        },
        button: {
            fontFamily: 'var(--font-body)',
            fontWeight: 600,
            fontSize: '0.875rem',
            textTransform: 'none',
            letterSpacing: '0.02em',
        },
        caption: {
            fontFamily: 'var(--font-body)',
            fontWeight: 400,
            fontSize: '0.75rem',
            lineHeight: 1.6,
        },
        overline: {
            fontFamily: 'var(--font-body)',
            fontWeight: 600,
            fontSize: '0.75rem',
            textTransform: 'uppercase',
            letterSpacing: '0.08em',
        },
    },
    palette: {
        mode: 'dark',
    },
});

export default theme;
