import React, { useState, useEffect } from 'react';
import {
  Box, 
  Button, 
  Paper, 
  Typography, 
  Table, 
  TableBody, 
  TableCell, 
  TableContainer, 
  TableHead, 
  TableRow,
  Chip,
  IconButton,
  TextField,
  InputAdornment,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  Grid,
  CircularProgress
} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import DeleteIcon from '@mui/icons-material/Delete';
import AddIcon from '@mui/icons-material/Add';
import Layout from '../components/Layout';
import { moviesAPI } from '../services/api';

const parseDuration = (isoDuration) => {
  if (!isoDuration || typeof isoDuration !== 'string') return 0;
  const hourMatch = isoDuration.match(/(\d+)H/);
  const minuteMatch = isoDuration.match(/(\d+)M/);
  const hours = hourMatch ? parseInt(hourMatch[1], 10) : 0;
  const minutes = minuteMatch ? parseInt(minuteMatch[1], 10) : 0;
  return hours * 60 + minutes;
};

const MoviesManagement = () => {
  const [loading, setLoading] = useState(true);
  const [movies, setMovies] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [openDeleteDialog, setOpenDeleteDialog] = useState(false);
  const [openAddDialog, setOpenAddDialog] = useState(false);
  const [selectedMovie, setSelectedMovie] = useState(null);
  const [saving, setSaving] = useState(false);

  const defaultMovieState = {
    title: '',
    duration: '',
    status: 'ACTIVE', 
    PosterURL: '',
    description: ''
  };
  const [movieForm, setMovieForm] = useState(defaultMovieState);

  useEffect(() => {
    const fetchMovies = async () => {
      try {
        setLoading(true);
        const data = await moviesAPI.getAllMovies();
        const mappedData = Array.isArray(data) ? data.map(movie => ({
          id: movie.id,
          title: movie.title,
          duration: typeof movie.duration === 'string' ? parseDuration(movie.duration) : movie.duration || 0,
          status: movie.status,
          PosterURL: movie.PosterURL || '',
          description: movie.description || ''
        })) : [];
        setMovies(mappedData);
      } catch (error) {
        console.error('Error fetching movies:', error);
      } finally {
        setLoading(false);
      }
    };
    fetchMovies();
  }, []);

  const filteredMovies = movies.filter(movie => movie.title.toLowerCase().includes(searchTerm.toLowerCase()));

  const handleOpenDeleteDialog = (movie) => {
    setSelectedMovie(movie);
    setOpenDeleteDialog(true);
  };

  const handleCloseDeleteDialog = () => {
    setOpenDeleteDialog(false);
    setSelectedMovie(null);
  };

  const handleDeleteMovie = async () => {
     if (!selectedMovie || !selectedMovie.id) {
    alert("Không thể xóa phim: ID không hợp lệ.");
    console.warn("selectedMovie is invalid:", selectedMovie);
    return;
  }

  try {
    console.log("Đang xoá phim có ID:", selectedMovie.id);
    await moviesAPI.deleteMovie(selectedMovie.id);

    const updatedMovies = await moviesAPI.getAllMovies();
    setMovies(updatedMovies);
    handleCloseDeleteDialog();

    alert(`Phim "${selectedMovie.title}" đã được xoá thành công.`);
  } catch (error) {
    console.error("Lỗi khi xóa phim:", error);
    if (error.response?.status === 404) {
      alert("Phim không tồn tại hoặc đã bị xóa trước đó.");
    } else if (error.response?.status === 400) {
      alert("Yêu cầu xoá không hợp lệ. Vui lòng kiểm tra lại.");
    } else {
      alert("Đã xảy ra lỗi khi xóa phim. Vui lòng thử lại sau.");
    }
  }
};
  const getStatusColor = (status) => status === 'ACTIVE' ? 'success' : 'default';
  const getStatusLabel = (status) => status === 'ACTIVE' ? 'Hoạt động' : 'Không hoạt động';

  const handleSaveMovie = async () => {
    if (!movieForm.title || !movieForm.duration) {
      alert('Vui lòng nhập đầy đủ thông tin bắt buộc (Tên phim và Thời lượng)');
      return;
    }
    setSaving(true);
    try {
      const durationMinutes = movieForm.duration ? parseInt(movieForm.duration, 10) : 0;
      const validDurationMinutes = isNaN(durationMinutes) ? 0 : durationMinutes;
      const hours = Math.floor(validDurationMinutes / 60);
      const minutes = validDurationMinutes % 60;
      let isoDuration = 'PT';
      if (hours > 0) isoDuration += `${hours}H`;
      if (minutes > 0) isoDuration += `${minutes}M`;
      if (hours === 0 && minutes === 0) isoDuration += '0H';

      const movieData = {
        title: movieForm.title,
        PosterURL: movieForm.PosterURL || '',
        duration: isoDuration,
        description: movieForm.description || ''
      };

      await moviesAPI.createMovie(movieData);
      const updatedMovies = await moviesAPI.getAllMovies();
      setMovies(updatedMovies);
      setMovieForm(defaultMovieState);
      setOpenAddDialog(false);
      alert('Thêm phim mới thành công!');
    } catch (error) {
      console.error('Error creating movie:', error);
      alert('Lỗi khi tạo phim mới.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <Layout>
      <Box sx={{ flexGrow: 1 }}>
        <Grid container spacing={2} alignItems="center" sx={{ mb: 3 }}>
          <Grid item xs={12} md={8}>
            <Typography variant="h4" component="h1" gutterBottom>
              Quản lý phim
            </Typography>
          </Grid>
          <Grid item xs={12} md={4} sx={{ textAlign: { xs: 'left', md: 'right' } }}>
            <Button 
              variant="contained" 
              color="primary" 
              startIcon={<AddIcon />}
              onClick={() => setOpenAddDialog(true)}
            >
              Thêm phim mới
            </Button>
          </Grid>
        </Grid>

        <Paper sx={{ p: 2, mb: 3 }}>
          <TextField
            fullWidth
            variant="outlined"
            placeholder="Tìm kiếm theo tên phim..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon />
                </InputAdornment>
              ),
            }}
          />
        </Paper>

        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>ID</TableCell>
                <TableCell>Tên phim</TableCell>
                <TableCell>Thời lượng (phút)</TableCell>
                <TableCell>Trạng thái</TableCell>
                <TableCell align="right">Hành động</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {loading ? (
                <TableRow>
                  <TableCell colSpan={5} align="center">
                    <CircularProgress size={24} />
                  </TableCell>
                </TableRow>
              ) : filteredMovies.length > 0 ? (
                filteredMovies.map((movie) => (
                  <TableRow key={movie.id}>
                    <TableCell>{movie.id}</TableCell>
                    <TableCell>{movie.title}</TableCell>
                    <TableCell>{movie.duration}</TableCell>
                    <TableCell>
                      <Chip 
                        label={getStatusLabel(movie.status)}
                        color={getStatusColor(movie.status)}
                        size="small"
                      />
                    </TableCell>
                    <TableCell align="right">
                      <IconButton 
                        color="error"
                        onClick={() => handleOpenDeleteDialog(movie)}
                        title="Xóa phim"
                      >
                        <DeleteIcon />
                      </IconButton>
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell colSpan={5} align="center">Không tìm thấy phim nào</TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </TableContainer>

        <Dialog open={openDeleteDialog} onClose={handleCloseDeleteDialog}>
          <DialogTitle>Xác nhận xóa phim</DialogTitle>
          <DialogContent>
            <DialogContentText>
              Bạn có chắc chắn muốn xóa phim "{selectedMovie?.title}"?
            </DialogContentText>
          </DialogContent>
          <DialogActions>
            <Button onClick={handleCloseDeleteDialog}>Hủy</Button>
            <Button onClick={handleDeleteMovie} color="error" autoFocus>
              Xóa
            </Button>
          </DialogActions>
        </Dialog>

        <Dialog open={openAddDialog} onClose={() => setOpenAddDialog(false)} maxWidth="md" fullWidth>
          <DialogTitle>Thêm phim mới</DialogTitle>
          <DialogContent>
            <Box component="form" sx={{ mt: 2 }}>
              <Grid container spacing={2}>
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label="Tên phim"
                    name="title"
                    value={movieForm.title}
                    onChange={(e) => setMovieForm({...movieForm, title: e.target.value})}
                    required
                  />
                </Grid>
                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label="Thời lượng (phút)"
                    name="duration"
                    type="number"
                    value={movieForm.duration}
                    onChange={(e) => setMovieForm({...movieForm, duration: e.target.value})}
                    required
                  />
                </Grid>
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label="Mô tả phim"
                    name="description"
                    multiline
                    rows={3}
                    value={movieForm.description}
                    onChange={(e) => setMovieForm({...movieForm, description: e.target.value})}
                  />
                </Grid>
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label="URL hình ảnh poster"
                    name="posterUrl"
                    placeholder="https://image.tmdb.org/t/p/w500/..."
                    value={movieForm.PosterURL || ''}
                    onChange={(e) => setMovieForm({...movieForm, PosterURL: e.target.value})}
                    helperText="Nhập URL hình ảnh từ internet (ví dụ: https://image.tmdb.org/t/p/w500/...)"
                    sx={{ mb: 2 }}
                  />
                </Grid>
                {movieForm.PosterURL && (
  <Grid item xs={12} sm={6}>
    <Typography variant="subtitle2" gutterBottom>Ảnh xem trước:</Typography>
    <img
      src={movieForm.PosterURL}
      alt="Poster Preview"
      style={{
        width: '100%',
        maxHeight: 200,
        objectFit: 'contain',
        borderRadius: 4,
        border: '1px solid #ccc',
        padding: 4
      }}
      onError={(e) => e.target.style.display = 'none'}
    />
  </Grid>
)}
              </Grid>
            </Box>
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setOpenAddDialog(false)}>Huỷ</Button>
            <Button 
              variant="contained" 
              color="primary"
              disabled={saving}
              onClick={handleSaveMovie}
            >
              {saving ? <CircularProgress size={24} /> : 'Lưu'}
            </Button>
          </DialogActions>
        </Dialog>
      </Box>
    </Layout>
  );
};

export default MoviesManagement;
