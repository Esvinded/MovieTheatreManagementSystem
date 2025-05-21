import React, { useState, useContext } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { 
  AppBar, 
  Box, 
  Button,
  CssBaseline, 
  Divider, 
  Drawer, 
  IconButton, 
  List, 
  ListItem, 
  ListItemButton, 
  ListItemIcon, 
  ListItemText, 
  Toolbar, 
  Typography
} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import LogoutIcon from '@mui/icons-material/Logout';
import DashboardIcon from '@mui/icons-material/Dashboard';
import MovieIcon from '@mui/icons-material/Movie';
import TheatersIcon from '@mui/icons-material/Theaters';
import DesktopWindowsIcon from '@mui/icons-material/DesktopWindows';
import WeekendIcon from '@mui/icons-material/Weekend';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import { AuthContext } from '../context/AuthContext';

const drawerWidth = 240;

const Layout = ({ children }) => {
  const [mobileOpen, setMobileOpen] = useState(false);
  const location = useLocation();
  const navigate = useNavigate();
  const { logout } = useContext(AuthContext);
  
  const handleDrawerToggle = () => {
    setMobileOpen(!mobileOpen);
  };
  
  const handleLogout = async () => {
    try {
      await logout();
      // Use navigate instead of window.location for better integration with React Router
      navigate('/login');
    } catch (error) {
      console.error('Logout failed:', error);
    }
  };

  // Cập nhật menu chỉ hiển thị các tính năng được hỗ trợ bởi backend Spring Boot
  const menuItems = [
    { text: 'Dashboard', icon: <DashboardIcon />, path: '/dashboard' },
    { text: 'Quản lý phim', icon: <MovieIcon />, path: '/admin/movies' },
    // Giữ lại showtimes vì có endpoints GET và POST /api/showtimes
    { text: 'Quản lý lịch chiếu', icon: <AccessTimeIcon />, path: '/admin/showtimes' },
    // Giữ lại theatres vì có endpoints GET và POST /api/theatre
    { text: 'Quản lý rạp chiếu', icon: <TheatersIcon />, path: '/admin/theatres' },
    // Giữ lại screens vì có endpoints trong ScreenController
    { text: 'Quản lý phòng chiếu', icon: <DesktopWindowsIcon />, path: '/admin/screens' },
    // Giữ lại seats vì có endpoints trong SeatController
    { text: 'Quản lý chỗ ngồi', icon: <WeekendIcon />, path: '/admin/seats' },
    // Đã loại bỏ menu quản lý nhân viên theo yêu cầu
  ];

  const drawer = (
    <div>
      <Toolbar>
        <Typography variant="h6" noWrap component="div">
          Movie Theatre Management
        </Typography>
      </Toolbar>
      <Divider />
      <List>
        {menuItems.map((item) => (
          <ListItem 
            key={item.text} 
            disablePadding
            component={Link} 
            to={item.path}
            sx={{ 
              color: 'inherit', 
              textDecoration: 'none',
              backgroundColor: location.pathname === item.path ? 'rgba(0, 0, 0, 0.08)' : 'transparent'
            }}
          >
            <ListItemButton>
              <ListItemIcon sx={{ color: location.pathname === item.path ? 'primary.main' : 'inherit' }}>
                {item.icon}
              </ListItemIcon>
              <ListItemText primary={item.text} />
            </ListItemButton>
          </ListItem>
        ))}
      </List>
    </div>
  );

  return (
    <Box sx={{ display: 'flex' }}>
      <CssBaseline />
      <AppBar
        position="fixed"
        sx={{
          width: { sm: `calc(100% - ${drawerWidth}px)` },
          ml: { sm: `${drawerWidth}px` },
        }}
      >
        <Toolbar>
          <IconButton
            color="inherit"
            aria-label="open drawer"
            edge="start"
            onClick={handleDrawerToggle}
            sx={{ mr: 2, display: { sm: 'none' } }}
          >
            <MenuIcon />
          </IconButton>
          <Typography variant="h6" noWrap component="div" sx={{ flexGrow: 1 }}>
            Movie Theatre Management System
          </Typography>
          <Typography variant="subtitle1" color="inherit" sx={{ mr: 2 }}>
            Admin Panel
          </Typography>
          <Button 
            color="inherit" 
            onClick={handleLogout}
            startIcon={<LogoutIcon />}
          >
            Đăng xuất
          </Button>
        </Toolbar>
      </AppBar>
      <Box
        component="nav"
        sx={{ width: { sm: drawerWidth }, flexShrink: { sm: 0 } }}
        aria-label="mailbox folders"
      >
        {/* Mobile drawer */}
        <Drawer
          variant="temporary"
          open={mobileOpen}
          onClose={handleDrawerToggle}
          ModalProps={{
            keepMounted: true, // Better mobile performance
          }}
          sx={{
            display: { xs: 'block', sm: 'none' },
            '& .MuiDrawer-paper': { boxSizing: 'border-box', width: drawerWidth },
          }}
        >
          {drawer}
        </Drawer>
        {/* Desktop drawer */}
        <Drawer
          variant="permanent"
          sx={{
            display: { xs: 'none', sm: 'block' },
            '& .MuiDrawer-paper': { boxSizing: 'border-box', width: drawerWidth },
          }}
          open
        >
          {drawer}
        </Drawer>
      </Box>
      <Box
        component="main"
        sx={{ 
          flexGrow: 1, 
          p: 3, 
          width: { sm: `calc(100% - ${drawerWidth}px)` },
          mt: 8
        }}
      >
        {children}
      </Box>
    </Box>
  );
};

export default Layout;