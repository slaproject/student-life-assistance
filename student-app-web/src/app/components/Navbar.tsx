// "use client";
// import React, { useState, useEffect } from 'react';
// import Link from 'next/link';
// import { usePathname } from 'next/navigation';
// import { useAuth } from '../AuthContext';
// import { 
// 	Menu, 
// 	Close, 
// 	CalendarToday, 
// 	CheckBox, 
// 	Timer, 
// 	AttachMoney, 
// 	Logout,
// 	Login as LoginIcon,
// 	School,
// 	Dashboard
// } from '@mui/icons-material';
// import { 
// 	AppBar, 
// 	Toolbar, 
// 	Typography, 
// 	Button, 
// 	Box, 
// 	IconButton,
// 	Menu as MuiMenu,
// 	MenuItem,
// 	useMediaQuery,
// 	useTheme
// } from '@mui/material';
// import '../globals.css';

// const Navbar = () => {
// 	const [isMenuOpen, setIsMenuOpen] = useState(false);
// 	const [scrolled, setScrolled] = useState(false);
// 	const pathname = usePathname();
// 	const { isAuthenticated, logout } = useAuth();
// 	const theme = useTheme();
// 	const isMobile = useMediaQuery(theme.breakpoints.down('md'));

// 	useEffect(() => {
// 		const handleScroll = () => {
// 			setScrolled(window.scrollY > 50);
// 		};
// 		window.addEventListener('scroll', handleScroll);
// 		return () => window.removeEventListener('scroll', handleScroll);
// 	}, []);

// 	const toggleMenu = () => {
// 		setIsMenuOpen(!isMenuOpen);
// 	};

// 	// Hide on auth pages
// 	if (pathname?.startsWith("/login") || pathname?.startsWith("/signup")) {
// 		return null;
// 	}

// 	const navItems = [
// 		{ name: 'Dashboard', href: '/dashboard', icon: Dashboard },
// 		{ name: 'Calendar', href: '/calendar', icon: CalendarToday },
// 		{ name: 'To-Do List', href: '/tasks', icon: CheckBox },
// 		{ name: 'Pomodoro', href: '/pomodoro', icon: Timer },
// 		{ name: 'Finance', href: '/finance', icon: AttachMoney },
// 	];

// 	return (
// 		<AppBar 
// 			position="fixed" 
// 			sx={{
// 				background: scrolled 
// 					? 'rgba(255, 255, 255, 0.95)' 
// 					: 'linear-gradient(90deg, #3f51b5, #1e88e5)',
// 				backdropFilter: scrolled ? 'blur(10px)' : 'none',
// 				boxShadow: scrolled ? '0 4px 20px rgba(0,0,0,0.1)' : 'none',
// 				transition: 'all 0.3s ease',
// 				zIndex: 50
// 			}}
// 		>
// 			<Toolbar sx={{ justifyContent: 'space-between', px: { xs: 2, md: 4 } }}>
// 				<Link href="/" style={{ textDecoration: 'none', display: 'flex', alignItems: 'center' }}>
// 					<School sx={{ 
// 						fontSize: 32, 
// 						color: scrolled ? '#3f51b5' : 'white',
// 						transition: 'color 0.3s ease'
// 					}} />
// 					<Typography 
// 						variant="h6" 
// 						sx={{ 
// 							ml: 1, 
// 							fontWeight: 'bold',
// 							color: scrolled ? '#1f2937' : 'white',
// 							transition: 'color 0.3s ease'
// 						}}
// 					>
// 						S.L.A.P
// 					</Typography>
// 				</Link>
				
// 				{!isMobile ? (
// 					<Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
// 						{navItems.map((item) => {
// 							const Icon = item.icon;
// 							const isActive = pathname === item.href;
// 							return (
// 								<Button
// 									key={item.name}
// 									component={Link}
// 									href={item.href}
// 									startIcon={<Icon />}
// 									sx={{
// 										color: isActive 
// 											? 'white' 
// 											: scrolled ? '#374151' : 'white',
// 										backgroundColor: isActive ? '#3f51b5' : 'transparent',
// 										borderRadius: 2,
// 										px: 2,
// 										py: 1,
// 										textTransform: 'none',
// 										fontWeight: 600,
// 										'&:hover': {
// 											backgroundColor: isActive ? '#2f43a0' : scrolled ? '#f3f4f6' : 'rgba(255,255,255,0.1)',
// 											color: isActive ? 'white' : scrolled ? '#1f2937' : 'white'
// 										},
// 										transition: 'all 0.2s ease'
// 									}}
// 								>
// 									{item.name}
// 								</Button>
// 							);
// 						})}
// 						{isAuthenticated ? (
// 							<Button
// 								startIcon={<Logout />}
// 								onClick={logout}
// 								sx={{
// 									color: scrolled ? '#374151' : 'white',
// 									backgroundColor: 'transparent',
// 									borderRadius: 2,
// 									px: 2,
// 									py: 1,
// 									textTransform: 'none',
// 									fontWeight: 600,
// 									ml: 1,
// 									'&:hover': {
// 										backgroundColor: scrolled ? '#fef2f2' : 'rgba(255,255,255,0.1)',
// 										color: scrolled ? '#dc2626' : 'white'
// 									},
// 									transition: 'all 0.2s ease'
// 								}}
// 							>
// 								Logout
// 							</Button>
// 						) : (
// 							<Button
// 								component={Link}
// 								href="/login"
// 								startIcon={<LoginIcon />}
// 								sx={{
// 									color: scrolled ? '#374151' : 'white',
// 									backgroundColor: 'transparent',
// 									borderRadius: 2,
// 									px: 2,
// 									py: 1,
// 									textTransform: 'none',
// 									fontWeight: 600,
// 									ml: 1,
// 									'&:hover': {
// 										backgroundColor: scrolled ? '#f3f4f6' : 'rgba(255,255,255,0.1)',
// 										color: scrolled ? '#1f2937' : 'white'
// 									},
// 									transition: 'all 0.2s ease'
// 								}}
// 							>
// 								Login
// 							</Button>
// 						)}
// 					</Box>
// 				) : (
// 					<IconButton
// 						onClick={toggleMenu}
// 						sx={{ 
// 							color: scrolled ? '#374151' : 'white',
// 							transition: 'color 0.3s ease'
// 						}}
// 					>
// 						{isMenuOpen ? <Close /> : <Menu />}
// 					</IconButton>
// 				)}
// 			</Toolbar>

// 			{/* Mobile menu */}
// 			{isMobile && isMenuOpen && (
// 				<Box sx={{ 
// 					backgroundColor: 'white', 
// 					boxShadow: '0 4px 20px rgba(0,0,0,0.1)',
// 					px: 2,
// 					py: 1
// 				}}>
// 					{navItems.map((item) => {
// 						const Icon = item.icon;
// 						const isActive = pathname === item.href;
// 						return (
// 							<Button
// 								key={item.name}
// 								component={Link}
// 								href={item.href}
// 								startIcon={<Icon />}
// 								fullWidth
// 								sx={{
// 									color: isActive ? 'white' : '#374151',
// 									backgroundColor: isActive ? '#3f51b5' : 'transparent',
// 									borderRadius: 2,
// 									px: 2,
// 									py: 1.5,
// 									textTransform: 'none',
// 									fontWeight: 600,
// 									justifyContent: 'flex-start',
// 									mb: 0.5,
// 									'&:hover': {
// 										backgroundColor: isActive ? '#2f43a0' : '#f3f4f6',
// 										color: isActive ? 'white' : '#1f2937'
// 									},
// 									transition: 'all 0.2s ease'
// 								}}
// 								onClick={() => setIsMenuOpen(false)}
// 							>
// 								{item.name}
// 							</Button>
// 						);
// 					})}
// 					{isAuthenticated ? (
// 						<Button
// 							startIcon={<Logout />}
// 							onClick={() => {
// 								logout();
// 								setIsMenuOpen(false);
// 							}}
// 							fullWidth
// 							sx={{
// 								color: '#374151',
// 								backgroundColor: 'transparent',
// 								borderRadius: 2,
// 								px: 2,
// 								py: 1.5,
// 								textTransform: 'none',
// 								fontWeight: 600,
// 								justifyContent: 'flex-start',
// 								mt: 0.5,
// 								'&:hover': {
// 									backgroundColor: '#fef2f2',
// 									color: '#dc2626'
// 								},
// 								transition: 'all 0.2s ease'
// 							}}
// 						>
// 							Logout
// 						</Button>
// 					) : (
// 						<Button
// 							component={Link}
// 							href="/login"
// 							startIcon={<LoginIcon />}
// 							onClick={() => setIsMenuOpen(false)}
// 							fullWidth
// 							sx={{
// 								color: '#374151',
// 								backgroundColor: 'transparent',
// 								borderRadius: 2,
// 								px: 2,
// 								py: 1.5,
// 								textTransform: 'none',
// 								fontWeight: 600,
// 								justifyContent: 'flex-start',
// 								mt: 0.5,
// 								'&:hover': {
// 									backgroundColor: '#f3f4f6',
// 									color: '#1f2937'
// 								},
// 								transition: 'all 0.2s ease'
// 							}}
// 						>
// 							Login
// 						</Button>
// 					)}
// 				</Box>
// 			)}
// 		</AppBar>
// 	);
// };

// export default Navbar;

"use client";
import React, { useState, useEffect } from 'react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useAuth } from '../AuthContext';
import { 
	Menu, 
	Close, 
	CalendarToday, 
	CheckBox, 
	Timer, 
	AttachMoney, 
	Logout,
	Login as LoginIcon,
	School,
	Dashboard
} from '@mui/icons-material';
import { 
	AppBar, 
	Toolbar, 
	Typography, 
	Button, 
	Box, 
	IconButton,
	useMediaQuery,
	useTheme
} from '@mui/material';
import '../globals.css';

const Navbar = () => {
	const [isMenuOpen, setIsMenuOpen] = useState(false);
	const [scrolled, setScrolled] = useState(false);
	const pathname = usePathname();
	const { isAuthenticated, logout } = useAuth();
	const theme = useTheme();
	const isMobile = useMediaQuery(theme.breakpoints.down('md'));

	useEffect(() => {
		const handleScroll = () => {
			setScrolled(window.scrollY > 50);
		};
		window.addEventListener('scroll', handleScroll);
		return () => window.removeEventListener('scroll', handleScroll);
	}, []);

	const toggleMenu = () => {
		setIsMenuOpen(!isMenuOpen);
	};

	// Hide on auth pages
	if (pathname?.startsWith("/login") || pathname?.startsWith("/signup")) {
		return null;
	}

	const navItems = [
		{ name: 'Dashboard', href: '/dashboard', icon: Dashboard },
		{ name: 'Calendar', href: '/calendar', icon: CalendarToday },
		{ name: 'To-Do List', href: '/tasks', icon: CheckBox },
		{ name: 'Pomodoro', href: '/pomodoro', icon: Timer },
		{ name: 'Finance', href: '/finance', icon: AttachMoney },
	];

  return (
		<AppBar 
			position="sticky" 
			sx={{
				background: '#000',
				backdropFilter: 'none',
				boxShadow: scrolled ? '0 4px 20px rgba(0,0,0,0.2)' : 'none',
				transition: 'all 0.3s ease',
				zIndex: 50
			}}
		>
			<Toolbar sx={{ justifyContent: 'space-between', px: { xs: 2, md: 4 } }}>
				<Link href="/" style={{ textDecoration: 'none', display: 'flex', alignItems: 'center' }}>
					<School sx={{ 
						fontSize: 32, 
						color: 'white',
						transition: 'color 0.3s ease'
					}} />
					<Typography 
						variant="h6" 
						sx={{ 
							ml: 1, 
							fontWeight: 'bold',
							color: 'white',
							transition: 'color 0.3s ease'
						}}
					>
						S.L.A.P
					</Typography>
				</Link>
				
				{!isMobile ? (
					<Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
						{navItems.map((item) => {
							const Icon = item.icon;
							const isActive = pathname === item.href;
							return (
								<Button
									key={item.name}
									component={Link}
									href={item.href}
									startIcon={<Icon />}
									sx={{
										color: 'white',
										backgroundColor: isActive ? 'rgba(255,255,255,0.18)' : 'transparent',
										borderRadius: 2,
										px: 2,
										py: 1,
										textTransform: 'none',
										fontWeight: 600,
										'&:hover': {
											backgroundColor: isActive ? 'rgba(255,255,255,0.24)' : 'rgba(255,255,255,0.12)',
											color: 'white'
										},
										transition: 'all 0.2s ease'
									}}
								>
									{item.name}
								</Button>
							);
						})}
						{isAuthenticated ? (
							<Button
								startIcon={<Logout />}
								onClick={logout}
								sx={{
									color: 'white',
									backgroundColor: 'transparent',
									borderRadius: 2,
									px: 2,
									py: 1,
									textTransform: 'none',
									fontWeight: 600,
									ml: 1,
									'&:hover': {
										backgroundColor: 'rgba(255,255,255,0.12)',
										color: 'white'
									},
									transition: 'all 0.2s ease'
								}}
							>
								Logout
							</Button>
						) : (
							<Button
								component={Link}
								href="/login"
								startIcon={<LoginIcon />}
								sx={{
									color: 'white',
									backgroundColor: 'transparent',
									borderRadius: 2,
									px: 2,
									py: 1,
									textTransform: 'none',
									fontWeight: 600,
									ml: 1,
									'&:hover': {
										backgroundColor: 'rgba(255,255,255,0.12)',
										color: 'white'
									},
									transition: 'all 0.2s ease'
								}}
							>
								Login
							</Button>
						)}
					</Box>
				) : (
					<IconButton
						onClick={toggleMenu}
						sx={{ 
							color: 'white',
							transition: 'color 0.3s ease'
						}}
					>
						{isMenuOpen ? <Close /> : <Menu />}
					</IconButton>
				)}
			</Toolbar>

			{/* Mobile menu */}
			{isMobile && isMenuOpen && (
				<Box sx={{ 
					backgroundColor: 'white', 
					boxShadow: '0 4px 20px rgba(0,0,0,0.1)',
					px: 2,
					py: 1
				}}>
					{navItems.map((item) => {
						const Icon = item.icon;
						const isActive = pathname === item.href;
						return (
							<Button
								key={item.name}
								component={Link}
								href={item.href}
								startIcon={<Icon />}
								fullWidth
								sx={{
									color: isActive ? 'white' : '#374151',
									backgroundColor: isActive ? '#111827' : 'transparent',
									borderRadius: 2,
									px: 2,
									py: 1.5,
									textTransform: 'none',
									fontWeight: 600,
									justifyContent: 'flex-start',
									mb: 0.5,
									'&:hover': {
										backgroundColor: isActive ? '#000000' : '#f3f4f6',
										color: isActive ? 'white' : '#1f2937'
									},
									transition: 'all 0.2s ease'
								}}
								onClick={() => setIsMenuOpen(false)}
							>
								{item.name}
							</Button>
						);
					})}
					{isAuthenticated ? (
						<Button
							startIcon={<Logout />}
							onClick={() => {
								logout();
								setIsMenuOpen(false);
							}}
							fullWidth
							sx={{
								color: '#374151',
								backgroundColor: 'transparent',
								borderRadius: 2,
								px: 2,
								py: 1.5,
								textTransform: 'none',
								fontWeight: 600,
								justifyContent: 'flex-start',
								mt: 0.5,
								'&:hover': {
									backgroundColor: '#fef2f2',
									color: '#dc2626'
								},
								transition: 'all 0.2s ease'
							}}
						>
							Logout
						</Button>
					) : (
						<Button
							component={Link}
							href="/login"
							startIcon={<LoginIcon />}
							onClick={() => setIsMenuOpen(false)}
							fullWidth
							sx={{
								color: '#374151',
								backgroundColor: 'transparent',
								borderRadius: 2,
								px: 2,
								py: 1.5,
								textTransform: 'none',
								fontWeight: 600,
								justifyContent: 'flex-start',
								mt: 0.5,
								'&:hover': {
									backgroundColor: '#f3f4f6',
									color: '#1f2937'
								},
								transition: 'all 0.2s ease'
							}}
						>
							Login
						</Button>
					)}
				</Box>
			)}
		</AppBar>
	);
};

export default Navbar;