import React, { useState, useEffect } from "react";
import { useLocation } from "react-router-dom";
import {
  Box,
  Button,
  Paper,
  Typography,
  Grid,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  Chip,
  IconButton,
  Tooltip,
  Stack,
  Alert,
  Snackbar,
} from "@mui/material";
import WeekendIcon from "@mui/icons-material/Weekend";
import EditIcon from "@mui/icons-material/Edit";
import SaveIcon from "@mui/icons-material/Save";
import Layout from "../components/Layout";
import { seatsAPI, screensAPI, theatresAPI } from "../services/api";

const SeatsManagement = () => {
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const screenIdParam = searchParams.get("screenId");

  // Trạng thái
  const [loading, setLoading] = useState(true);
  const [theatres, setTheatres] = useState([]);
  const [screens, setScreens] = useState([]);
  const [selectedTheatre, setSelectedTheatre] = useState("");
  const [selectedScreen, setSelectedScreen] = useState(screenIdParam || "");
  const [seats, setSeats] = useState([]);
  const [currentScreen, setCurrentScreen] = useState(null);
  const [editMode, setEditMode] = useState(false);
  const [openResetDialog, setOpenResetDialog] = useState(false);
  const [openDeleteDialog, setOpenDeleteDialog] = useState(false);
  const [selectedSeat, setSelectedSeat] = useState(null);
  const [openSnackbar, setOpenSnackbar] = useState(false);
  const [snackbarMessage, setSnackbarMessage] = useState("");

  // Định nghĩa trạng thái ghế theo Spring Boot Status enum
  const seatStatus = {
    ACTIVE: "ACTIVE",
    INACTIVE: "INACTIVE",
    // Không còn sử dụng AVAILABLE, MAINTENANCE vì không có trong backend Status enum
  };

  // Lấy dữ liệu từ API
  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);

        // Lấy danh sách rạp chiếu
        const theatresData = await theatresAPI.getAllTheatres();
        setTheatres(theatresData);

        // Lấy danh sách tất cả phòng chiếu
        const screensData = await screensAPI.getAllScreens();

        // Thêm thông tin tên rạp cho các phòng chiếu
        const enhancedScreens = await Promise.all(
          screensData.map(async (screen) => {
            try {
              const theatre = await theatresAPI.getTheatreById(
                screen.theatreId,
              );
              // ScreenDto không có trường capacity, vì vậy ta sẽ dùng số cố định 5x5 làm mặc định
              return {
                ...screen,
                theatreName: theatre ? theatre.name : "Unknown",
                // Sử dụng số cố định vì không có capacity trong ScreenDto
                rows: 5,
                cols: 5,
                // Loại bỏ capacity khỏi hiển thị UI
                capacity: null,
              };
            } catch (error) {
              console.error(
                `Error fetching theatre for screen ${screen.id}:`,
                error,
              );
              return {
                ...screen,
                theatreName: "Unknown",
                rows: 5,
                cols: 5,
                // Loại bỏ capacity khỏi hiển thị UI
                capacity: null,
              };
            }
          }),
        );

        setScreens(enhancedScreens);

        // Nếu có screenIdParam, thiết lập selectedTheatre tương ứng
        if (screenIdParam) {
          const selectedScreenData = enhancedScreens.find(
            (screen) => screen.id === parseInt(screenIdParam),
          );
          if (selectedScreenData) {
            setSelectedTheatre(selectedScreenData.theatreId.toString());
            setSelectedScreen(screenIdParam);
          }
        }

        setLoading(false);
      } catch (error) {
        console.error("Error fetching initial data:", error);
        setLoading(false);
      }
    };

    fetchData();
  }, [screenIdParam]);

  // Lọc màn hình theo rạp đã chọn
  const filteredScreens = screens.filter(
    (screen) =>
      !selectedTheatre || screen.theatreId === parseInt(selectedTheatre),
  );

  // Tải ghế khi chọn phòng chiếu
  useEffect(() => {
    const fetchSeats = async () => {
      if (selectedScreen) {
        setLoading(true);

        // Tìm thông tin phòng chiếu
        const screenData = screens.find(
          (screen) => screen.id === parseInt(selectedScreen),
        );
        setCurrentScreen(screenData);

        try {
          // Lấy dữ liệu ghế từ API
          const seatsData = await seatsAPI.getSeatsByScreen(
            parseInt(selectedScreen),
          );

          if (seatsData && seatsData.length > 0) {
            // Chuyển đổi dữ liệu từ API thành định dạng cần thiết
            const formattedSeats = seatsData.map((seat, index) => {
              // Tính toán vị trí trong grid
              const rowLabels = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");
              const rowIndex = rowLabels.indexOf(seat.rowLabel);
              const colIndex = seat.colNumber - 1;

              return {
                id: seat.id.toString(),
                row: seat.rowLabel,
                col: seat.colNumber,
                rowIndex:
                  rowIndex >= 0
                    ? rowIndex
                    : Math.floor(index / screenData.cols),
                colIndex: colIndex >= 0 ? colIndex : index % screenData.cols,
                status: seat.status,
              };
            });

            setSeats(formattedSeats);
          } else {
            // Nếu không có ghế từ API, tạo mẫu dựa trên thông tin phòng chiếu
            if (screenData) {
              const rows = screenData.rows;
              const cols = screenData.cols;
              const rowLabels = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");

              let generatedSeats = [];

              for (let i = 0; i < rows; i++) {
                for (let j = 0; j < cols; j++) {
                  generatedSeats.push({
                    id: `gen_${rowLabels[i]}${j + 1}`,
                    row: rowLabels[i],
                    col: j + 1,
                    rowIndex: i,
                    colIndex: j,
                    status: seatStatus.ACTIVE,
                  });
                }
              }

              setSeats(generatedSeats);
            }
          }
        } catch (error) {
          console.error("Error fetching seats:", error);
        } finally {
          setLoading(false);
        }
      } else {
        setSeats([]);
        setCurrentScreen(null);
      }
    };

    fetchSeats();
  }, [selectedScreen, screens, seatStatus.ACTIVE]);

  // Xử lý thay đổi rạp
  const handleTheatreChange = (event) => {
    setSelectedTheatre(event.target.value);
    setSelectedScreen("");
  };

  // Xử lý thay đổi phòng chiếu
  const handleScreenChange = (event) => {
    setSelectedScreen(event.target.value);
  };

  // Bắt đầu chế độ chỉnh sửa
  const handleStartEditMode = () => {
    setEditMode(true);
  };

  const handleSaveChanges = async () => {
    try {
      // Lọc ra các ghế đã thay đổi và có ID thực (không phải ID được tạo tự động)
      const realSeats = seats.filter((seat) => !seat.id.startsWith("gen_"));

      // Cập nhật từng ghế thông qua API
      await Promise.all(
        realSeats.map(async (seat) => {
          // Chuẩn bị dữ liệu cho API theo SeatUpdateRequest
          // Backend SeatUpdateRequest chỉ yêu cầu trường status
          const seatData = {
            status: seat.status
          };

          try {
            // Nếu ghế có ID, cập nhật
            return await seatsAPI.updateSeat(parseInt(seat.id), seatData);
          } catch (error) {
            console.error(`Error updating seat ${seat.id}:`, error);
          }
        }),
      );

      // Các ghế mới tạo (có ID bắt đầu bằng 'gen_')
      const newSeats = seats.filter((seat) => seat.id.startsWith("gen_"));

      // Tạo các ghế mới thông qua API
      await Promise.all(
        newSeats.map(async (seat) => {
          // Chuẩn bị dữ liệu cho API theo SeatCreateRequest
          const seatData = {
            rowLabel: seat.row,
            colNumber: seat.col,
            screenId: parseInt(selectedScreen), // Bắt buộc có khi tạo mới ghế
            status: seat.status || "ACTIVE", // Thêm trường status vì backend yêu cầu trong SeatCreateRequest
          };

          return null;
        }),
      );

      setEditMode(false);
      setSnackbarMessage("Đã lưu cấu hình ghế ngồi thành công!");
      setOpenSnackbar(true);

      // Sau khi thêm/sửa ghế => gọi lại API để sync thay vì dùng state local
      // Đảm bảo dữ liệu luôn nhất quán từ backend
      const updatedSeats = await seatsAPI.getSeatsByScreen(
        parseInt(selectedScreen),
      );
      setSeats(
        updatedSeats.map((seat) => ({
          id: seat.id.toString(),
          row: seat.rowLabel,
          col: seat.colNumber,
          status: seat.status,
        })),
      );
    } catch (error) {
      console.error("Error saving seats configuration:", error);
      setSnackbarMessage("Đã xảy ra lỗi khi lưu cấu hình ghế!");
      setOpenSnackbar(true);
    }
  };

  // Hủy chỉnh sửa
  const handleCancelEdit = () => {
    // Tải lại dữ liệu ghế
    setEditMode(false);
    setSelectedScreen(selectedScreen);
  };

  // Mở dialog xóa ghế
  const handleOpenDeleteDialog = (seat) => {
    if (editMode) {
      setSelectedSeat(seat);
      setOpenDeleteDialog(true);
    }
  };

  // Đóng dialog xóa ghế
  const handleCloseDeleteDialog = () => {
    setOpenDeleteDialog(false);
    setSelectedSeat(null);
  };
  
  const handleDeleteSeat = () => {
    handleCloseDeleteDialog();
    setSnackbarMessage("Chức năng không được hỗ trợ");
    setOpenSnackbar(true);
  };

  // Thay đổi trạng thái ghế
  const handleToggleSeatStatus = (seat) => {
    if (editMode) {
      const updatedSeats = seats.map((s) => {
        if (s.id === seat.id) {
          // Chỉ có 2 trạng thái ACTIVE và INACTIVE theo Status enum
          const newStatus =
            s.status === seatStatus.ACTIVE
              ? seatStatus.INACTIVE
              : seatStatus.ACTIVE;
          return { ...s, status: newStatus };
        }
        return s;
      });
      setSeats(updatedSeats);
    }
  };

  // Loại bỏ hàm handleChangeSeatType vì loại ghế không có trong SeatDto

  // Mở dialog reset ghế
  const handleOpenResetDialog = () => {
    setOpenResetDialog(true);
  };

  // Đóng dialog reset ghế
  const handleCloseResetDialog = () => {
    setOpenResetDialog(false);
  };

  // Reset tất cả ghế
  const handleResetSeats = () => {
    // Trong trường hợp thực tế, đây sẽ là API call
    setSelectedScreen(selectedScreen); // Tải lại dữ liệu ghế
    setOpenResetDialog(false);
  };

  // Lấy màu cho trạng thái ghế
  const getSeatColor = (status, type) => {
    // Chỉ có 2 trạng thái theo Status enum: ACTIVE hoặc INACTIVE

    return status === seatStatus.ACTIVE ? "#66BB6A" : "#BDBDBD"; // Xanh lá hoặc xám
  };

  // Lấy nhãn cho trạng thái ghế
  const getSeatLabel = (status) => {
    return status === seatStatus.ACTIVE ? "Đang hoạt động" : "Không hoạt động";
  };

  // Lấy icon cho ghế
  const getSeatIcon = (status) => {
    // Kích thước icon tiêu chuẩn
    const iconSize = 30;

    // Độ mờ dựa trên trạng thái hoạt động
    const opacity = status === seatStatus.ACTIVE ? 1 : 0.3;

    return <WeekendIcon sx={{ fontSize: iconSize, opacity }} />;
  };

  // Đóng snackbar
  const handleCloseSnackbar = () => {
    setOpenSnackbar(false);
  };

  // Sắp xếp ghế theo hàng và cột
  const sortedSeats = [...seats].sort((a, b) => {
    if (a.rowIndex !== b.rowIndex) {
      return a.rowIndex - b.rowIndex;
    }
    return a.colIndex - b.colIndex;
  });

  // Nhóm ghế theo hàng
  const seatsByRow = sortedSeats.reduce((acc, seat) => {
    if (!acc[seat.row]) {
      acc[seat.row] = [];
    }
    acc[seat.row].push(seat);
    return acc;
  }, {});

  return (
    <Layout>
      <Box sx={{ flexGrow: 1 }}>
        {/* Tiêu đề */}
        <Typography variant="h4" component="h1" gutterBottom>
          Quản lý chỗ ngồi
        </Typography>

        {/* Bộ lọc */}
        <Paper sx={{ p: 3, mb: 3 }}>
          <Grid container spacing={3}>
            <Grid item xs={12} md={6}>
              <FormControl fullWidth>
                <InputLabel id="theatre-select-label">Chọn rạp</InputLabel>
                <Select
                  labelId="theatre-select-label"
                  value={selectedTheatre}
                  onChange={handleTheatreChange}
                  label="Chọn rạp"
                  disabled={loading || editMode}
                >
                  <MenuItem value="">Chọn rạp</MenuItem>
                  {theatres.map((theatre) => (
                    <MenuItem key={theatre.id} value={theatre.id.toString()}>
                      {theatre.name}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>

            <Grid item xs={12} md={6}>
              <FormControl fullWidth>
                <InputLabel id="screen-select-label">
                  Chọn phòng chiếu
                </InputLabel>
                <Select
                  labelId="screen-select-label"
                  value={selectedScreen}
                  onChange={handleScreenChange}
                  label="Chọn phòng chiếu"
                  disabled={!selectedTheatre || loading || editMode}
                >
                  <MenuItem value="">Chọn phòng chiếu</MenuItem>
                  {filteredScreens.map((screen) => (
                    <MenuItem key={screen.id} value={screen.id.toString()}>
                      {screen.name}{" "}
                      {/* Không hiển thị capacity vì ScreenDto không có trường này */}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>
          </Grid>
        </Paper>

        {/* Nút chức năng */}
        {selectedScreen && (
          <Paper sx={{ p: 2, mb: 3 }}>
            <Grid container spacing={2} alignItems="center">
              <Grid item xs={12} md={8}>
                <Typography variant="h6">
                  {currentScreen?.theatreName} - {currentScreen?.name}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Số chỗ ngồi: {currentScreen?.capacity || seats.length}
                </Typography>
              </Grid>
              <Grid item xs={12} md={4} sx={{ textAlign: "right" }}>
                {editMode ? (
                  <Stack
                    direction="row"
                    spacing={1}
                    justifyContent={{ xs: "flex-start", md: "flex-end" }}
                  >
                    <Button
                      variant="outlined"
                      color="error"
                      onClick={handleCancelEdit}
                    >
                      Hủy
                    </Button>
                    <Button
                      variant="contained"
                      color="warning"
                      onClick={handleOpenResetDialog}
                    >
                      Reset
                    </Button>
                    <Button
                      variant="contained"
                      color="primary"
                      startIcon={<SaveIcon />}
                      onClick={handleSaveChanges}
                    >
                      Lưu
                    </Button>
                  </Stack>
                ) : (
                  <Button
                    variant="contained"
                    color="primary"
                    startIcon={<EditIcon />}
                    onClick={handleStartEditMode}
                    disabled={loading}
                  >
                    Chỉnh sửa ghế
                  </Button>
                )}
              </Grid>
            </Grid>
          </Paper>
        )}

        {/* Chú thích */}
        {selectedScreen && (
          <Paper sx={{ p: 2, mb: 3 }}>
            {/* Đã loại bỏ phần chú thích loại ghế vì không có trong SeatDto */}

            <Typography variant="subtitle1" gutterBottom>
              Trạng thái:
            </Typography>
            <Stack direction="row" spacing={2}>
              <Chip
                icon={<WeekendIcon />}
                label="Hoạt động"
                sx={{
                  bgcolor: getSeatColor(seatStatus.ACTIVE),
                  color: "white",
                }}
              />
              <Chip
                icon={<WeekendIcon />}
                label="Không hoạt động"
                sx={{
                  bgcolor: getSeatColor(seatStatus.INACTIVE),
                  color: "white",
                }}
              />
            </Stack>

            {editMode && (
              <Alert severity="info" sx={{ mt: 2 }}>
                <Typography variant="body2">
                  <strong>Hướng dẫn chỉnh sửa:</strong>
                </Typography>
                <Typography variant="body2">
                  - Nhấp 1 lần: Đổi trạng thái ghế (Hoạt động ➝ Không hoạt động)
                </Typography>
                <Typography variant="body2">
                  - Các thay đổi chỉ có hiệu lực sau khi nhấn Lưu
                </Typography>
              </Alert>
            )}
          </Paper>
        )}

        {/* Hiển thị ghế */}
        {selectedScreen && !loading ? (
          <Paper
            sx={{
              p: 3,
              mb: 3,
              display: "flex",
              flexDirection: "column",
              alignItems: "center",
            }}
          >
            {/* Màn hình */}
            <Box
              sx={{
                width: "80%",
                height: "30px",
                bgcolor: "#90CAF9",
                borderRadius: "4px",
                display: "flex",
                justifyContent: "center",
                alignItems: "center",
                mb: 4,
              }}
            >
              <Typography variant="body2">Màn hình</Typography>
            </Box>

            {/* Ghế ngồi */}
            <Box sx={{ width: "100%", overflowX: "auto" }}>
              {Object.keys(seatsByRow)
                .sort()
                .map((row) => (
                  <Box
                    key={row}
                    sx={{
                      display: "flex",
                      justifyContent: "center",
                      mb: 1,
                      flexWrap: "wrap",
                    }}
                  >
                    <Box
                      sx={{
                        width: "30px",
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "center",
                        mr: 2,
                      }}
                    >
                      <Typography variant="body2" fontWeight="bold">
                        {row}
                      </Typography>
                    </Box>

                    {/* Ghế trong hàng */}
                    {seatsByRow[row].map((seat) => (
                      <Tooltip
                        key={seat.id}
                        title={
                          <>
                            <Typography variant="body2">
                              Ghế: {seat.id}
                            </Typography>
                            <Typography variant="body2">
                              Hàng: {seat.row}
                            </Typography>
                            <Typography variant="body2">
                              Cột: {seat.col}
                            </Typography>
                            <Typography variant="body2">
                              Trạng thái:{" "}
                              {seat.status === seatStatus.ACTIVE
                                ? "Hoạt động"
                                : "Không hoạt động"}
                            </Typography>
                            {editMode && (
                              <Typography variant="body2">
                                Nhấp để thay đổi trạng thái
                              </Typography>
                            )}
                          </>
                        }
                        arrow
                      >
                        <IconButton
                          sx={{
                            color: "white",
                            bgcolor: getSeatColor(seat.status),
                            m: 0.5,
                            "&:hover": {
                              bgcolor: editMode
                                ? "rgba(0, 0, 0, 0.08)"
                                : getSeatColor(seat.status),
                            },
                          }}
                          onClick={() => handleToggleSeatStatus(seat)}
                          onContextMenu={(e) => {
                            e.preventDefault();
                            // Loại bỏ việc thay đổi loại ghế
                          }}
                          onDoubleClick={() => handleOpenDeleteDialog(seat)}
                          disabled={!editMode}
                        >
                          {getSeatIcon(seat.status)}
                        </IconButton>
                      </Tooltip>
                    ))}
                  </Box>
                ))}
            </Box>
          </Paper>
        ) : selectedScreen && loading ? (
          <Paper sx={{ p: 3, textAlign: "center" }}>
            <Typography>Đang tải cấu hình ghế...</Typography>
          </Paper>
        ) : (
          <Paper sx={{ p: 3, textAlign: "center" }}>
            <Typography>
              Vui lòng chọn rạp và phòng chiếu để xem cấu hình ghế
            </Typography>
          </Paper>
        )}

        {/* Dialog reset ghế */}
        <Dialog open={openResetDialog} onClose={handleCloseResetDialog}>
          <DialogTitle>Xác nhận reset cấu hình ghế</DialogTitle>
          <DialogContent>
            <DialogContentText>
              Bạn có chắc chắn muốn reset toàn bộ cấu hình ghế về mặc định? Tất
              cả thay đổi hiện tại sẽ bị mất.
            </DialogContentText>
          </DialogContent>
          <DialogActions>
            <Button onClick={handleCloseResetDialog}>Hủy</Button>
            <Button onClick={handleResetSeats} color="warning" autoFocus>
              Reset
            </Button>
          </DialogActions>
        </Dialog>

        {/* Dialog xóa ghế */}
        <Dialog open={openDeleteDialog} onClose={handleCloseDeleteDialog}>
          <DialogTitle>Xác nhận xóa ghế</DialogTitle>
          <DialogContent>
            <DialogContentText>
              Bạn có chắc chắn muốn xóa ghế {selectedSeat?.id}?
            </DialogContentText>
          </DialogContent>
          <DialogActions>
            <Button onClick={handleCloseDeleteDialog}>Hủy</Button>
            <Button onClick={handleDeleteSeat} color="error" autoFocus>
              Xóa
            </Button>
          </DialogActions>
        </Dialog>

        {/* Snackbar thông báo */}
        <Snackbar
          open={openSnackbar}
          autoHideDuration={4000}
          onClose={handleCloseSnackbar}
          message={snackbarMessage}
        />
      </Box>
    </Layout>
  );
};

export default SeatsManagement;
