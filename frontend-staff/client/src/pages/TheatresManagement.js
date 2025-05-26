import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
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
  Card,
  CardContent,
  CardActions,
  Divider,
  CircularProgress,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
} from "@mui/material";
import SearchIcon from "@mui/icons-material/Search";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import AddIcon from "@mui/icons-material/Add";
import TheatersIcon from "@mui/icons-material/Theaters";
import LocationOnIcon from "@mui/icons-material/LocationOn";
import ScreenShareIcon from "@mui/icons-material/ScreenShare";
import Layout from "../components/Layout";
import { theatresAPI } from "../services/api";

const TheatresManagement = () => {
  const [theatres, setTheatres] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
  const [openDialog, setOpenDialog] = useState(false);
  // Biến confirmDelete đã bị loại bỏ vì không hỗ trợ xóa rạp trong backend
  const [selectedTheatre, setSelectedTheatre] = useState(null);
  const [newTheatre, setNewTheatre] = useState({
    name: "",
    address: "",
    status: "ACTIVE", // Mặc định là ACTIVE khi thêm mới
    totalScreen: 1    // Thêm totalScreen với giá trị tối thiểu là 1 theo yêu cầu của backend
  });

  // Fetch theatres
  const fetchTheatres = async () => {
    setLoading(true);
    try {
      const data = await theatresAPI.getAllTheatres();
      console.log("Fetched theatres:", data);
      setTheatres(data);
      setError("");
    } catch (err) {
      console.error("Error fetching theatres:", err);
      setError("Không thể tải dữ liệu rạp chiếu. Vui lòng thử lại sau.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTheatres();
  }, []);

  // Filter theatres based on search term
  const filteredTheatres = theatres.filter(
    (theatre) =>
      theatre.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      theatre.address.toLowerCase().includes(searchTerm.toLowerCase()),
  );

  // Handle theatre form change
  const handleNewTheatreChange = (e) => {
    const { name, value } = e.target;
    setNewTheatre((prev) => ({ ...prev, [name]: value }));
  };

  // Handle edit theatre
  const handleEditTheatre = (theatre) => {
    setSelectedTheatre(theatre);
    setNewTheatre({
      name: theatre.name,
      address: theatre.address,
      // Không gửi status khi cập nhật, chỉ cần name và address
      // status chỉ được sử dụng cho giao diện
    });
    setOpenDialog(true);
  };

  // Reset form
  const handleCloseDialog = () => {
    setOpenDialog(false);
    setSelectedTheatre(null);
    setNewTheatre({
      name: "",
      address: "",
      status: "ACTIVE",
      totalScreen: 1    // Thêm totalScreen khi reset form
    });
  };

  // Handle save theatre
  const handleSaveTheatre = async () => {
    try {
      console.log("Đang lưu dữ liệu rạp chiếu...");
      console.log("Dữ liệu gửi đi:", newTheatre);
      
      if (selectedTheatre) {
        // Update existing theatre
        console.log("Cập nhật rạp chiếu ID:", selectedTheatre.id);
        await theatresAPI.updateTheatre(selectedTheatre.id, newTheatre);
      } else {
        // Create new theatre
        console.log("Tạo rạp chiếu mới");
        await theatresAPI.createTheatre(newTheatre);
      }

      handleCloseDialog();
      fetchTheatres();
    } catch (err) {
      console.error("Error saving theatre:", err);
      
      // Hiển thị thông tin lỗi chi tiết
      if (err.response) {
        console.error("Response data:", err.response.data);
        console.error("Response status:", err.response.status);
        console.error("Response headers:", err.response.headers);
        
        // Hiển thị thông báo lỗi chi tiết hơn
        if (err.response.data && err.response.data.message) {
          setError(`Lỗi: ${err.response.data.message}`);
        } else {
          setError(`Không thể lưu dữ liệu rạp chiếu. Lỗi ${err.response.status}`);
        }
      } else {
        setError("Không thể lưu dữ liệu rạp chiếu. Vui lòng thử lại sau.");
      }
    }
  };

  // Backend không hỗ trợ API xóa rạp chiếu - loại bỏ theo yêu cầu

  return (
    <Layout>
      <Box sx={{ p: 3 }}>
        <Typography
          variant="h4"
          component="h1"
          gutterBottom
          sx={{ display: "flex", alignItems: "center" }}
        >
          <TheatersIcon sx={{ mr: 1 }} />
          Quản lý rạp chiếu
        </Typography>

        {/* Search and Add button */}
        <Box sx={{ mb: 3, display: "flex", justifyContent: "space-between" }}>
          <TextField
            placeholder="Tìm kiếm rạp chiếu..."
            variant="outlined"
            size="small"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon />
                </InputAdornment>
              ),
            }}
            sx={{ width: "300px" }}
          />
          <Button
            variant="contained"
            color="primary"
            startIcon={<AddIcon />}
            onClick={() => setOpenDialog(true)}
          >
            Thêm rạp chiếu mới
          </Button>
        </Box>

        {/* Error message */}
        {error && (
          <Paper
            sx={{
              p: 2,
              mb: 3,
              bgcolor: "error.light",
              color: "error.contrastText",
            }}
          >
            <Typography>{error}</Typography>
          </Paper>
        )}

        {/* Loading indicator */}
        {loading ? (
          <Box sx={{ display: "flex", justifyContent: "center", mt: 4 }}>
            <CircularProgress />
          </Box>
        ) : (
          // Theatres list
          <>
            {filteredTheatres.length === 0 ? (
              <Paper sx={{ p: 3, textAlign: "center" }}>
                <Typography>Không tìm thấy rạp chiếu nào</Typography>
              </Paper>
            ) : (
              <TableContainer component={Paper}>
                <Table sx={{ minWidth: 650 }}>
                  <TableHead>
                    <TableRow>
                      <TableCell>ID</TableCell>
                      <TableCell>Tên rạp</TableCell>
                      <TableCell>Địa chỉ</TableCell>
                      <TableCell>Trạng thái</TableCell>
                      <TableCell align="center">Thao tác</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {filteredTheatres.map((theatre) => (
                      <TableRow key={theatre.id}>
                        <TableCell>{theatre.id}</TableCell>
                        <TableCell>{theatre.name}</TableCell>
                        <TableCell>{theatre.address}</TableCell>
                        <TableCell>
                          <Chip
                            label={
                              theatre.status === "ACTIVE"
                                ? "Hoạt động"
                                : "Không hoạt động"
                            }
                            color={
                              theatre.status === "ACTIVE"
                                ? "success"
                                : "default"
                            }
                            size="small"
                            sx={{ cursor: 'default' }}
                          />
                        </TableCell>
                        <TableCell align="center">
                          <IconButton
                            color="primary"
                            onClick={() => handleEditTheatre(theatre)}
                            size="small"
                          >
                            <EditIcon fontSize="small" />
                          </IconButton>
                          {/* Chức năng xóa rạp đã bị loại bỏ vì không có API hỗ trợ trong backend */}
                          <IconButton
                            component={Link}
                            to={`/admin/screens?theatreId=${theatre.id}`}
                            color="info"
                            size="small"
                            title="Quản lý phòng chiếu"
                          >
                            <ScreenShareIcon fontSize="small" />
                          </IconButton>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            )}
          </>
        )}

        {/* Add/Edit Theatre Dialog */}
        <Dialog open={openDialog} onClose={handleCloseDialog} fullWidth>
          <DialogTitle>
            {selectedTheatre ? "Chỉnh sửa rạp chiếu" : "Thêm rạp chiếu mới"}
          </DialogTitle>
          <DialogContent>
            <TextField
              autoFocus
              margin="dense"
              label="Tên rạp"
              name="name"
              fullWidth
              variant="outlined"
              value={newTheatre.name}
              onChange={handleNewTheatreChange}
              sx={{ mb: 2 }}
            />
            <TextField
              label="Địa chỉ"
              name="address"
              fullWidth
              variant="outlined"
              value={newTheatre.address}
              onChange={handleNewTheatreChange}
              multiline
              rows={2}
              sx={{ mb: 2 }}
            />
            {!selectedTheatre && (
              <TextField
                label="Số màn hình"
                name="totalScreen"
                type="number"
                fullWidth
                variant="outlined"
                value={newTheatre.totalScreen || 1}
                onChange={handleNewTheatreChange}
                helperText="Số lượng màn hình tối thiểu là 1"
                InputProps={{ inputProps: { min: 1 } }}
                sx={{ mb: 2 }}
              />
            )}
          </DialogContent>
          <DialogActions>
            <Button onClick={handleCloseDialog}>Hủy</Button>
            <Button
              onClick={handleSaveTheatre}
              variant="contained"
              color="primary"
            >
              Lưu
            </Button>
          </DialogActions>
        </Dialog>

        {/* Hộp thoại xác nhận xóa đã bị loại bỏ vì không có API hỗ trợ trong backend */}
      </Box>
    </Layout>
  );
};

export default TheatresManagement;
