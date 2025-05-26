import React, { useState, useEffect } from "react";
import { useLocation, Link } from "react-router-dom";
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
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Tabs,
  Tab,
  Tooltip,
  Snackbar,
  Alert,
  CircularProgress,
} from "@mui/material";
import SearchIcon from "@mui/icons-material/Search";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import AddIcon from "@mui/icons-material/Add";
import ScreenShareIcon from "@mui/icons-material/ScreenShare";
import Layout from "../components/Layout";
import { screensAPI, theatresAPI } from "../services/api";

const ScreensManagement = () => {
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const theatreIdParam = searchParams.get("theatreId");

  // Trạng thái
  const [loading, setLoading] = useState(true);
  const [screens, setScreens] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [openAddDialog, setOpenAddDialog] = useState(false);
  const [openEditDialog, setOpenEditDialog] = useState(false);
  const [theatres, setTheatres] = useState([]);
  const [selectedTheatre, setSelectedTheatre] = useState(theatreIdParam || "");
  const [newScreen, setNewScreen] = useState({
    name: "",
    theatreId: theatreIdParam || "",
    capacity: 100,
  });
  const [editScreen, setEditScreen] = useState({
    id: null,
    name: "",
    status: "ACTIVE",
  });
  
  // State cho snackbar thông báo
  const [snackbar, setSnackbar] = useState({
    open: false,
    message: "",
    severity: "success" // success, error, warning, info
  });

  // Lấy dữ liệu từ API
  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);

        // Lấy danh sách tất cả rạp chiếu
        const theatresData = await theatresAPI.getAllTheatres();
        setTheatres(theatresData);

        // Lấy danh sách tất cả phòng chiếu
        let screensData;
        if (theatreIdParam) {
          // Nếu có tham số theatreId, chỉ lấy phòng chiếu của rạp đó
          screensData = await screensAPI.getScreensByTheatre(
            parseInt(theatreIdParam),
          );
          setSelectedTheatre(theatreIdParam);
        } else {
          // Nếu không, lấy tất cả phòng chiếu
          screensData = await screensAPI.getAllScreens();
        }

        // Thêm thông tin tên rạp cho mỗi phòng chiếu
        const screensWithTheatreNames = await Promise.all(
          screensData.map(async (screen) => {
            try {
              const theatre = await theatresAPI.getTheatreById(
                screen.theatreId,
              );
              return {
                ...screen,
                theatreName: theatre ? theatre.name : "Unknown",
              };
            } catch (error) {
              console.error(
                `Error fetching theatre for screen ${screen.id}:`,
                error,
              );
              return {
                ...screen,
                theatreName: "Unknown",
              };
            }
          }),
        );

        setScreens(screensWithTheatreNames);
      } catch (error) {
        console.error("Error fetching data:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [theatreIdParam]);

  // Lọc phòng chiếu theo rạp và tìm kiếm
  const filteredScreens = screens.filter((screen) => {
    const matchesTheatre = selectedTheatre
      ? screen.theatreId === parseInt(selectedTheatre)
      : true;
    const matchesSearch =
      screen.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      (screen.capacity &&
        screen.capacity.toString().includes(searchTerm));

    return matchesTheatre && matchesSearch;
  });

  // Xử lý thay đổi rạp
  const handleTheatreChange = (event) => {
    setSelectedTheatre(event.target.value);
  };

  // Mở dialog thêm phòng chiếu
  const handleOpenAddDialog = () => {
    setNewScreen({
      ...newScreen,
      theatreId: selectedTheatre,
    });
    setOpenAddDialog(true);
  };

  // Đóng dialog thêm phòng chiếu
  const handleCloseAddDialog = () => {
    setOpenAddDialog(false);
    setNewScreen({
      name: "",
      theatreId: selectedTheatre || "",
      capacity: "",
      status: "ACTIVE", // Sử dụng enum Status của backend (ACTIVE/INACTIVE)
    });
  };

  // Cập nhật thông tin phòng chiếu mới
  const handleNewScreenChange = (e) => {
    const { name, value } = e.target;
    setNewScreen({
      ...newScreen,
      [name]: value,
    });
  };

  // Thêm phòng chiếu mới qua API
  const handleAddScreen = async () => {
    // Kiểm tra dữ liệu
    if (!newScreen.name || !newScreen.theatreId || !newScreen.capacity) {
      return;
    }

    try {
      // Chuẩn bị dữ liệu cho API theo ScreenCreateRequest
      const screenData = {
        name: newScreen.name,
        theatreId: parseInt(newScreen.theatreId),
        capacity: parseInt(newScreen.capacity),
      };

      // Gọi API để tạo phòng chiếu mới
      await screensAPI.createScreen(screenData);

      // Sau khi thêm phòng => gọi lại API để sync thay vì chỉ setScreens([...])
      // Đảm bảo dữ liệu luôn nhất quán từ backend
      const updatedScreens = await screensAPI.getAllScreens();
      setScreens(updatedScreens);

      handleCloseAddDialog();
    } catch (error) {
      console.error("Error creating screen:", error);
      // Thêm xử lý lỗi nếu cần
    }
  };

  // Mở dialog chỉnh sửa phòng chiếu
  const handleOpenEditDialog = (screen) => {
    setEditScreen({
      id: screen.id,
      name: screen.name,
      status: screen.status || "ACTIVE"
    });
    setOpenEditDialog(true);
  };

  // Đóng dialog chỉnh sửa
  const handleCloseEditDialog = () => {
    setOpenEditDialog(false);
    setEditScreen({
      id: null,
      name: "",
      status: "ACTIVE"
    });
  };

  // Cập nhật thông tin chỉnh sửa
  const handleEditScreenChange = (e) => {
    const { name, value } = e.target;
    setEditScreen({
      ...editScreen,
      [name]: value,
    });
  };

  // Lưu thay đổi phòng chiếu
  const handleUpdateScreen = async () => {
    if (!editScreen.id || !editScreen.name || !editScreen.status) {
      return;
    }

    try {
      // Chuẩn bị dữ liệu theo ScreenUpdateRequest
      const screenData = {
        name: editScreen.name,
        status: editScreen.status
      };

      // Gọi API để cập nhật phòng chiếu
      await screensAPI.updateScreen(editScreen.id, screenData);

      // Cập nhật lại dữ liệu từ backend
      const updatedScreens = await screensAPI.getAllScreens();
      setScreens(updatedScreens);

      handleCloseEditDialog();
    } catch (error) {
      console.error(`Error updating screen ${editScreen.id}:`, error);
    }
  };

  // Đóng snackbar thông báo
  const handleCloseSnackbar = () => {
    setSnackbar({...snackbar, open: false});
  };
  
  // Xóa phòng chiếu
  const handleDeleteScreen = async (screenId) => {
    // Hỏi xác nhận trước khi xóa
    if (window.confirm("Bạn có chắc chắn muốn xóa phòng chiếu này?")) {
      try {
        // Lưu trữ trạng thái phòng chiếu hiện tại trước khi xóa
        const originalScreens = [...screens];
        
        // Hiển thị trạng thái loading
        setLoading(true);
        
        // Cập nhật UI trước khi gọi API (Optimistic UI update)
        setScreens(prevScreens => prevScreens.filter(screen => screen.id !== screenId));
        
        try {
          // Gọi API xóa
          await screensAPI.deleteScreen(screenId);
          console.log(`Đã xóa phòng chiếu ID=${screenId} thành công`);
          
          // Hiển thị thông báo thành công
          setSnackbar({
            open: true,
            message: "Đã xóa phòng chiếu thành công",
            severity: "success"
          });
          
          // Tải lại danh sách phù hợp với lọc hiện tại
          if (selectedTheatre) {
            const updatedScreens = await screensAPI.getScreensByTheatre(parseInt(selectedTheatre));
            setScreens(updatedScreens);
          } else {
            const updatedScreens = await screensAPI.getAllScreens();
            setScreens(updatedScreens);
          }
        } catch (error) {
          console.error(`Lỗi khi xóa phòng chiếu ${screenId}:`, error);
          
          // Xử lý lỗi 500 từ server
          if (error.response && error.response.status === 500) {
            console.log("Server trả về lỗi 500, có thể phòng chiếu đã bị xóa");
            
            // Thử tải lại danh sách để kiểm tra
            try {
              if (selectedTheatre) {
                const updatedScreens = await screensAPI.getScreensByTheatre(parseInt(selectedTheatre));
                setScreens(updatedScreens);
              } else {
                const updatedScreens = await screensAPI.getAllScreens();
                setScreens(updatedScreens);
              }
              
              setSnackbar({
                open: true,
                message: "Phòng chiếu đã bị xóa nhưng có lỗi xảy ra trên máy chủ",
                severity: "warning"
              });
            } catch (loadError) {
              console.error("Không thể tải lại danh sách phòng chiếu:", loadError);
            }
          } else {
            // Khôi phục trạng thái ban đầu nếu xóa thất bại
            setScreens(originalScreens);
            
            setSnackbar({
              open: true,
              message: "Không thể xóa phòng chiếu. Phòng này có thể đang được sử dụng hoặc đã có lịch chiếu.",
              severity: "error"
            });
          }
        }
      } catch (error) {
        console.error("Lỗi xử lý thao tác xóa phòng chiếu:", error);
        
        setSnackbar({
          open: true,
          message: "Đã xảy ra lỗi không xác định",
          severity: "error"
        });
      } finally {
        setLoading(false);
      }
    }
  };

  return (
    <Layout>
      <Box sx={{ flexGrow: 1 }}>
        {/* Tiêu đề */}
        <Grid container spacing={2} alignItems="center" sx={{ mb: 3 }}>
          <Grid item xs={12} md={8}>
            <Typography variant="h4" component="h1" gutterBottom>
              Quản lý phòng chiếu
            </Typography>
          </Grid>
          <Grid
            item
            xs={12}
            md={4}
            sx={{ textAlign: { xs: "left", md: "right" } }}
          >
            <Button
              variant="contained"
              color="primary"
              startIcon={<AddIcon />}
              onClick={handleOpenAddDialog}
              disabled={!selectedTheatre}
            >
              Thêm phòng chiếu
            </Button>
          </Grid>
        </Grid>

        {/* Bộ lọc */}
        <Paper sx={{ p: 2, mb: 3 }}>
          <Grid container spacing={2}>
            <Grid item xs={12} md={6}>
              <FormControl fullWidth>
                <InputLabel id="theatre-filter-label">Rạp chiếu</InputLabel>
                <Select
                  labelId="theatre-filter-label"
                  value={selectedTheatre}
                  onChange={handleTheatreChange}
                  label="Rạp chiếu"
                >
                  <MenuItem value="">Tất cả rạp</MenuItem>
                  {theatres.map((theatre) => (
                    <MenuItem key={theatre.id} value={theatre.id}>
                      {theatre.name}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                variant="outlined"
                placeholder="Tìm kiếm phòng chiếu..."
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
            </Grid>
          </Grid>
        </Paper>

        {/* Bảng danh sách phòng chiếu */}
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Tên phòng</TableCell>
                <TableCell>Sức chứa</TableCell>
                <TableCell>Trạng thái</TableCell>
                <TableCell align="right">Hành động</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {loading ? (
                <TableRow>
                  <TableCell colSpan={5} align="center">
                    Đang tải...
                  </TableCell>
                </TableRow>
              ) : filteredScreens.length > 0 ? (
                filteredScreens.map((screen) => (
                  <TableRow key={screen.id}>
                    <TableCell>{screen.name}</TableCell>
                    <TableCell>{screen.capacity}</TableCell>
                    <TableCell>
                      <Chip
                        label={screen.status}
                        color={screen.status === "ACTIVE" ? "success" : "error"}
                        size="small"
                      />
                    </TableCell>
                    <TableCell align="right">
                      <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 1 }}>
                        <IconButton 
                          color="primary" 
                          onClick={() => handleOpenEditDialog(screen)}
                        >
                          <EditIcon />
                        </IconButton>
                        <IconButton 
                          color="error" 
                          onClick={() => handleDeleteScreen(screen.id)}
                        >
                          <DeleteIcon />
                        </IconButton>
                        <IconButton
                          color="info"
                          component={Link}
                          to={`/seats?screenId=${screen.id}`}
                        >
                          <ScreenShareIcon />
                        </IconButton>
                      </Box>
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell colSpan={7} align="center">
                    {selectedTheatre
                      ? "Không tìm thấy phòng chiếu nào"
                      : "Vui lòng chọn rạp chiếu"}
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </TableContainer>

        {/* Dialog thêm phòng chiếu */}
        <Dialog
          open={openAddDialog}
          onClose={handleCloseAddDialog}
          maxWidth="sm"
          fullWidth
        >
          <DialogTitle>Thêm phòng chiếu mới</DialogTitle>
          <DialogContent>
            <FormControl fullWidth margin="normal">
              <InputLabel id="theatre-select-label">Rạp chiếu</InputLabel>
              <Select
                labelId="theatre-select-label"
                name="theatreId"
                value={newScreen.theatreId}
                onChange={handleNewScreenChange}
                label="Rạp chiếu"
              >
                <MenuItem value="">Chọn rạp</MenuItem>
                {theatres.map((theatre) => (
                  <MenuItem key={theatre.id} value={theatre.id}>
                    {theatre.name}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>

            <TextField
              margin="normal"
              name="name"
              label="Tên phòng chiếu"
              type="text"
              fullWidth
              variant="outlined"
              value={newScreen.name}
              onChange={handleNewScreenChange}
            />

            <TextField
              margin="normal"
              name="capacity"
              label="Sức chứa"
              type="number"
              fullWidth
              variant="outlined"
              value={newScreen.capacity}
              onChange={handleNewScreenChange}
              inputProps={{ min: 1 }}
            />
          </DialogContent>
          <DialogActions>
            <Button onClick={handleCloseAddDialog}>Hủy</Button>
            <Button
              onClick={handleAddScreen}
              variant="contained"
              color="primary"
              disabled={!newScreen.name || !newScreen.theatreId || !newScreen.capacity}
            >
              Thêm phòng
            </Button>
          </DialogActions>
        </Dialog>

        {/* Dialog chỉnh sửa phòng chiếu */}
        <Dialog
          open={openEditDialog}
          onClose={handleCloseEditDialog}
          maxWidth="sm"
          fullWidth
        >
          <DialogTitle>Chỉnh sửa phòng chiếu</DialogTitle>
          <DialogContent>
            <TextField
              margin="normal"
              name="name"
              label="Tên phòng chiếu"
              type="text"
              fullWidth
              variant="outlined"
              value={editScreen.name}
              onChange={handleEditScreenChange}
            />

            <FormControl fullWidth margin="normal">
              <InputLabel id="status-select-label">Trạng thái</InputLabel>
              <Select
                labelId="status-select-label"
                name="status"
                value={editScreen.status}
                onChange={handleEditScreenChange}
                label="Trạng thái"
              >
                <MenuItem value="ACTIVE">ACTIVE</MenuItem>
                <MenuItem value="INACTIVE">INACTIVE</MenuItem>
              </Select>
            </FormControl>
          </DialogContent>
          <DialogActions>
            <Button onClick={handleCloseEditDialog}>Hủy</Button>
            <Button
              onClick={handleUpdateScreen}
              variant="contained"
              color="primary"
              disabled={!editScreen.name || !editScreen.status}
            >
              Lưu thay đổi
            </Button>
          </DialogActions>
        </Dialog>
        
        {/* Snackbar thông báo */}
        <Snackbar
          open={snackbar.open}
          autoHideDuration={5000}
          onClose={handleCloseSnackbar}
          anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
        >
          <Alert onClose={handleCloseSnackbar} severity={snackbar.severity} sx={{ width: '100%' }}>
            {snackbar.message}
          </Alert>
        </Snackbar>
      </Box>
    </Layout>
  );
};

export default ScreensManagement;
