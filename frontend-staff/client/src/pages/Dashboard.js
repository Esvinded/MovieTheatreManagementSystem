import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import {
  Box, 
  Grid, 
  Paper, 
  Typography, 
  Card, 
  CardContent, 
  CardActions,
  Button,
  Divider,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Chip,
  CircularProgress
} from '@mui/material';
import MovieIcon from '@mui/icons-material/Movie';
import TheatersIcon from '@mui/icons-material/Theaters';
import ScreenShareIcon from '@mui/icons-material/ScreenShare';
import WeekendIcon from '@mui/icons-material/Weekend';
import EventIcon from '@mui/icons-material/Event';
import StarIcon from '@mui/icons-material/Star';
import Layout from '../components/Layout';
import { dashboardAPI, moviesAPI, showtimesAPI, screensAPI } from '../services/api';
// Thay thế import từ date-fns
const format = (date, formatStr) => {
  const d = new Date(date);
  // Định dạng đơn giản ngày/tháng/năm giờ:phút
  return `${d.getDate().toString().padStart(2, '0')}/${(d.getMonth() + 1).toString().padStart(2, '0')}/${d.getFullYear()} ${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`;
};

const Dashboard = () => {
  // Dashboard data states
  const [loading, setLoading] = useState(true);
  const [stats, setStats] = useState({
    movies: 0,
    showtimes: 0,
    theatres: 0,
    screens: 0
  });
  const [latestMovies, setLatestMovies] = useState([]);
  const [upcomingShowtimes, setUpcomingShowtimes] = useState([]);

  // Fetch data from API
  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        setLoading(true);



        // Lấy dữ liệu thống kê từ backend
        const statsData = await dashboardAPI.getSummary();
        setStats({
          movies: statsData.totalMovies || 0,
          showtimes: statsData.totalShowtimes || 0,
          theatres: statsData.totalTheatres || 0,
          screens: statsData.totalScreens || 0
        });

        // Lấy danh sách phim mới nhất
        try {
          const moviesData = await moviesAPI.getAllMovies();

          // Chuyển đổi dữ liệu phim để phù hợp với giao diện
          const formattedMovies = moviesData.map(movie => ({
            id: movie.id,
            title: movie.title,
            // Xử lý định dạng ISO-8601 Duration (PT1H30M) từ Spring Boot backend
            duration: typeof movie.duration === 'string' && movie.duration.startsWith('PT')
              ? (() => {
                  // Phân tích định dạng như PT1H30M
                  const hourMatch = movie.duration.match(/(\d+)H/);
                  const minuteMatch = movie.duration.match(/(\d+)M/);

                  const hours = hourMatch ? parseInt(hourMatch[1]) : 0;
                  const minutes = minuteMatch ? parseInt(minuteMatch[1]) : 0;

                  return hours * 60 + minutes;
                })()
              : movie.duration || '',
            status: movie.status || 'ACTIVE',
            PosterURL: movie.PosterURL || ''
          }));

          // Sử dụng dữ liệu từ API
          setLatestMovies(formattedMovies.slice(0, 5));
        } catch (error) {
          console.error('Error fetching movies:', error);
          setLatestMovies([]);
        }

        // Lấy danh sách lịch chiếu sắp tới từ backend với thông tin đầy đủ
        try {
          // Lấy movies và screens trước
          const [moviesData, screensData] = await Promise.all([
            moviesAPI.getAllMovies(),
            screensAPI.getAllScreens()
          ]);
          
          // Lấy showtimes từ tất cả phim để hiển thị chính xác số lượng
          let showtimesData = [];
          if (moviesData && moviesData.length > 0) {
            try {
              // Lấy lịch chiếu từ phim đầu tiên
              const firstMovieShowtimes = await showtimesAPI.getShowtimesByMovie(moviesData[0].id);
              console.log("Lịch chiếu từ phim đầu tiên:", firstMovieShowtimes);
              
              if (Array.isArray(firstMovieShowtimes)) {
                showtimesData = firstMovieShowtimes;
              }
              
              // Lấy thêm lịch chiếu từ các phim khác (tối đa 3 phim) để đảm bảo có đủ dữ liệu
              const maxMoviesToFetch = Math.min(moviesData.length, 3);
              for (let i = 1; i < maxMoviesToFetch; i++) {
                try {
                  const additionalShowtimes = await showtimesAPI.getShowtimesByMovie(moviesData[i].id);
                  if (Array.isArray(additionalShowtimes)) {
                    showtimesData = [...showtimesData, ...additionalShowtimes];
                  }
                } catch (movieError) {
                  console.error(`Error fetching showtimes for movie ${moviesData[i].id}:`, movieError);
                }
              }
            } catch (error) {
              console.error('Error fetching showtimes:', error);
              // Tiếp tục với mảng rỗng nếu không lấy được showtimes
            }
          }

          // Lọc lịch chiếu để hiển thị các suất sắp diễn ra (thời gian bắt đầu >= hiện tại)
          const now = new Date();
          const upcomingShowtimesData = showtimesData.filter(showtime => {
            const startTime = new Date(showtime.startTime);
            return startTime >= now;
          });

          // Chuyển đổi dữ liệu lịch chiếu và kết hợp với thông tin phim và phòng chiếu
          const formattedShowtimes = upcomingShowtimesData.map(showtime => {
            // Tìm thông tin phim và phòng chiếu từ dữ liệu đã lấy
            const movie = moviesData.find(m => m.id === showtime.movieId) || {};
            const screen = screensData.find(s => s.id === showtime.screenId) || {};

            return {
              id: showtime.id,
              startTime: showtime.startTime,
              endTime: showtime.endTime,
              movieId: showtime.movieId,
              screenId: showtime.screenId,
              movieTitle: movie.title || `Phim ID: ${showtime.movieId}`,
              screenName: screen.name || `Phòng ID: ${showtime.screenId}`,
              status: showtime.status || 'ACTIVE'
            };
          });

          // Sắp xếp theo thời gian bắt đầu và lấy 5 suất gần nhất
          formattedShowtimes.sort((a, b) => new Date(a.startTime) - new Date(b.startTime));
          setUpcomingShowtimes(formattedShowtimes.slice(0, 5));
        } catch (error) {
          console.error('Error fetching showtimes:', error);
          setUpcomingShowtimes([]);
        }
      } catch (error) {
        console.error('Error fetching dashboard data:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, []);

  // Lấy màu cho trạng thái phim (dựa trên Spring Boot status enum: ACTIVE/INACTIVE)
  const getStatusColor = (status) => {
    switch (status) {
      case 'ACTIVE':
        return 'success';
      case 'INACTIVE':
        return 'error';
      default:
        return 'default';
    }
  };

  // Lấy nhãn hiển thị cho trạng thái phim
  const getStatusLabel = (status) => {
    switch (status) {
      case 'ACTIVE':
        return 'Đang hoạt động';
      case 'INACTIVE':
        return 'Không hoạt động';
      default:
        return status;
    }
  };

  return (
    <Layout>
      <Box sx={{ flexGrow: 1 }}>
        {/* Tiêu đề */}
        <Typography variant="h4" component="h1" gutterBottom>
          Dashboard Quản Lý Rạp Chiếu Phim
        </Typography>

        {/* Thống kê tổng quan */}
        <Grid container spacing={3} sx={{ mb: 4 }}>
          <Grid item xs={12} sm={6} md={4} lg={20} sx={{width: '20%'}}>
            <Paper sx={{ p: 2, display: 'flex', flexDirection: 'column', height: 140, bgcolor: '#e1bee7' }}>
              <Typography variant="h6" color="text.secondary" sx={{ mb: 1 }}>
                Phòng chiếu
              </Typography>
              <Typography variant="h3" component="div" sx={{ fontWeight: 'bold', my: 'auto' }}>
                {loading ? '...' : stats.screens}
              </Typography>
              <Button 
                size="small" 
                component={Link} 
                to="/admin/screens"
                sx={{ alignSelf: 'flex-end', mt: 'auto' }}
              >
                Xem chi tiết
              </Button>
            </Paper>
          </Grid>
          <Grid item xs={12} sm={6} md={4} lg={20} sx={{width: '20%'}}>
            <Paper sx={{ p: 2, display: 'flex', flexDirection: 'column', height: 140, bgcolor: '#bbdefb' }}>
              <Typography variant="h6" color="text.secondary" sx={{ mb: 1 }}>
                Phim
              </Typography>
              <Typography variant="h3" component="div" sx={{ fontWeight: 'bold', my: 'auto' }}>
                {loading ? '...' : stats.movies}
              </Typography>
              <Button 
                size="small" 
                component={Link} 
                to="/admin/movies"
                sx={{ alignSelf: 'flex-end', mt: 'auto' }}
              >
                Xem chi tiết
              </Button>
            </Paper>
          </Grid>
          
          <Grid item xs={12} sm={6} md={4} lg={20} sx={{width: '20%'}}>
            <Paper sx={{ p: 2, display: 'flex', flexDirection: 'column', height: 140, bgcolor: '#fff9c4' }}>
              <Typography variant="h6" color="text.secondary" sx={{ mb: 1 }}>
                Rạp chiếu
              </Typography>
              <Typography variant="h3" component="div" sx={{ fontWeight: 'bold', my: 'auto' }}>
                {loading ? '...' : stats.theatres}
              </Typography>
              <Button cd
                size="small" 
                component={Link} 
                to="/admin/theatres"
                sx={{ alignSelf: 'flex-end', mt: 'auto' }}
              >
                Xem chi tiết
              </Button>
            </Paper>
          </Grid>

        </Grid>

        <Grid container spacing={4}>
          {/* Phim mới nhất */}
          <Grid item xs={12} md={6}>
            <Card>
              <CardContent>
                <Typography variant="h5" component="div" gutterBottom>
                  <MovieIcon sx={{ mr: 1, verticalAlign: 'middle' }} />
                  Phim mới nhất
                </Typography>
                <Divider sx={{ mb: 2 }} />
                <List>
                  {loading ? (
                    <ListItem sx={{ justifyContent: 'center' }}>
                      <CircularProgress size={24} />
                    </ListItem>
                  ) : latestMovies.length > 0 ? (
                    latestMovies.map((movie) => (
                      <ListItem key={movie.id} divider>
                        <ListItemIcon>
                          <StarIcon color="primary" />
                        </ListItemIcon>
                        <ListItemText 
                          primary={movie.title} 
                          secondary={movie.duration ? `Thời lượng: ${typeof movie.duration === 'number' ? 
                            `${Math.floor(movie.duration / 60)}h ${movie.duration % 60}m` : 
                            String(movie.duration).replace('PT', '').replace('H', 'h ').replace('M', 'm')}` : 'N/A'} 
                        />
                        <Chip 
                          label={movie.status === 'ACTIVE' ? 'Đang chiếu' : 'Không hoạt động'}
                          color={movie.status === 'ACTIVE' ? 'success' : 'default'}
                          size="small"
                        />
                      </ListItem>
                    ))
                  ) : (
                    <ListItem>
                      <ListItemText primary="Không có phim nào." />
                    </ListItem>
                  )}
                </List>
              </CardContent>
              <CardActions>
                <Button 
                  component={Link} 
                  to="/admin/movies" 
                  size="small" 
                  sx={{ ml: 'auto' }}
                >
                  Xem tất cả phim
                </Button>
              </CardActions>
            </Card>
          </Grid>

          {/* Lịch chiếu sắp tới */}
          <Grid item xs={12} md={6}>
            <Card>
              <CardContent>
                <Typography variant="h5" component="div" gutterBottom>
                  <EventIcon sx={{ mr: 1, verticalAlign: 'middle' }} />
                  Lịch chiếu sắp tới
                </Typography>
                <Divider sx={{ mb: 2 }} />
                <List>
                  {loading ? (
                    <ListItem sx={{ justifyContent: 'center' }}>
                      <CircularProgress size={24} />
                    </ListItem>
                  ) : upcomingShowtimes.length > 0 ? (
                    upcomingShowtimes.map((showtime) => (
                      <ListItem key={showtime.id} divider>
                        <ListItemIcon>
                          <TheatersIcon color="primary" />
                        </ListItemIcon>
                        <ListItemText 
                          primary={showtime.movieTitle || `Phim ID: ${showtime.movieId || 'N/A'}`} 
                          secondary={showtime.screenName || `Màn hình ID: ${showtime.screenId || 'N/A'}`}
                        />
                        <Typography variant="body2" color="text.secondary">
                          {showtime.startTime ? format(new Date(showtime.startTime), 'dd/MM/yyyy HH:mm') : 'Thời gian không xác định'}
                        </Typography>
                      </ListItem>
                    ))
                  ) : (
                    <ListItem>
                      <ListItemText primary="Không có lịch chiếu nào sắp tới." />
                    </ListItem>
                  )}
                </List>
              </CardContent>
              <CardActions>
                <Button 
                  component={Link} 
                  to="/admin/showtimes" 
                  size="small" 
                  sx={{ ml: 'auto' }}
                >
                  Xem tất cả lịch chiếu
                </Button>
              </CardActions>
            </Card>
          </Grid>

          {/* Liên kết nhanh */}
          <Grid item xs={12}>
            <Paper sx={{ p: 3 }}>
              <Typography variant="h6" gutterBottom>
                Liên kết nhanh
              </Typography>
              <Divider sx={{ mb: 2 }} />
              <Grid container spacing={2}>
                <Grid item xs={6} sm={4} md={3}>
                  <Button
                    variant="outlined"
                    component={Link}
                    to="/admin/screens"
                    fullWidth
                    startIcon={<ScreenShareIcon />}
                    sx={{ justifyContent: 'flex-start', py: 1 }}
                  >
                    Quản lý phòng chiếu
                  </Button>
                </Grid>
                <Grid item xs={6} sm={4} md={3}>
                  <Button
                    variant="outlined"
                    component={Link}
                    to="/admin/movies"
                    fullWidth
                    startIcon={<MovieIcon />}
                    sx={{ justifyContent: 'flex-start', py: 1 }}
                  >
                    Quản lý phim
                  </Button>
                </Grid>
                <Grid item xs={6} sm={4} md={3}>
                  <Button
                    variant="outlined"
                    component={Link}
                    to="/admin/showtimes/create"
                    fullWidth
                    startIcon={<EventIcon />}
                    sx={{ justifyContent: 'flex-start', py: 1 }}
                  >
                    Tạo lịch chiếu
                  </Button>
                </Grid>
                <Grid item xs={6} sm={4} md={3}>
                  <Button
                    variant="outlined"
                    component={Link}
                    to="/admin/theatres"
                    fullWidth
                    startIcon={<TheatersIcon />}
                    sx={{ justifyContent: 'flex-start', py: 1 }}
                  >
                    Quản lý rạp
                  </Button>
                </Grid>
                <Grid item xs={6} sm={4} md={3}>
                  <Button
                    variant="outlined"
                    component={Link}
                    to="/admin/seats"
                    fullWidth
                    startIcon={<WeekendIcon />}
                    sx={{ justifyContent: 'flex-start', py: 1 }}
                  >
                    Quản lý ghế ngồi
                  </Button>
                </Grid>
              </Grid>
            </Paper>
          </Grid>
        </Grid>
      </Box>
    </Layout>
  );
};

export default Dashboard;