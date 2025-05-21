import { useState } from "react";
import {
  Box,
  Button,
  TextField,
  Typography,
  Paper,
  Container,
  Grid,
  Alert,
  Snackbar,
  CircularProgress,
} from "@mui/material";
import { authAPI } from "../services/api";

export default function AuthStaffPage() {
  // State cho đăng nhập
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [email, setEmail] = useState("");

  // State cho hiển thị form
  const [isRegister, setIsRegister] = useState(false);
  const [showResetPassword, setShowResetPassword] = useState(false);

  // State cho thông báo
  const [openSnackbar, setOpenSnackbar] = useState(false);
  const [snackbarMessage, setSnackbarMessage] = useState("");
  const [snackbarSeverity, setSnackbarSeverity] = useState("success");

  // State cho loading
  const [isLoading, setIsLoading] = useState(false);

  // Kiểm tra email hợp lệ
  const isValidEmail = (email) => {
    const emailRegex = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$/;
    return emailRegex.test(email);
  };

  // Đăng nhập
  const handleLogin = async (e) => {
    e.preventDefault();

    if (!username || !password) {
      setSnackbarMessage("Vui lòng nhập tên đăng nhập và mật khẩu!");
      setSnackbarSeverity("error");
      setOpenSnackbar(true);
      return;
    }

    setIsLoading(true);
    try {
      const response = await authAPI.loginStaff({
        username,
        password,
      });

      setSnackbarMessage("Đăng nhập thành công!");
      setSnackbarSeverity("success");
      setOpenSnackbar(true);

      // Lưu thông tin đăng nhập vào localStorage
      localStorage.setItem("staff", JSON.stringify(response));
      localStorage.setItem("isAuthenticated", "true");
      localStorage.setItem("userRole", "STAFF");

      // Chuyển hướng về trang dashboard sau đăng nhập
      setTimeout(() => {
        window.location.href = "/dashboard";
      }, 1000);
    } catch (error) {
      let errorMessage = "Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin.";
      if (error.response && error.response.data) {
        // Nếu server trả về message cụ thể
        errorMessage =
          typeof error.response.data === "string"
            ? error.response.data
            : error.response.data.message || errorMessage;
      }
      setSnackbarMessage(errorMessage);
      setSnackbarSeverity("error");
      setOpenSnackbar(true);
    } finally {
      setIsLoading(false);
    }
  };

  // Đăng ký
  const handleRegister = async (e) => {
    e.preventDefault();

    if (!username || !password || !email) {
      setSnackbarMessage("Vui lòng điền đầy đủ thông tin!");
      setSnackbarSeverity("error");
      setOpenSnackbar(true);
      return;
    }

    // Kiểm tra định dạng email
    if (!isValidEmail(email)) {
      setSnackbarMessage("Email không hợp lệ!");
      setSnackbarSeverity("error");
      setOpenSnackbar(true);
      return;
    }

    // Kiểm tra độ dài mật khẩu
    if (password.length < 6) {
      setSnackbarMessage("Mật khẩu phải có ít nhất 6 ký tự!");
      setSnackbarSeverity("error");
      setOpenSnackbar(true);
      return;
    }

    setIsLoading(true);
    try {
      await authAPI.registerStaff({
        username,
        password,
        email,
      });

      setSnackbarMessage("Đăng ký thành công! Vui lòng đăng nhập.");
      setSnackbarSeverity("success");
      setOpenSnackbar(true);

      // Xóa dữ liệu form sau khi đăng ký thành công
      setUsername("");
      setPassword("");
      setEmail("");

      // Chuyển về form đăng nhập
      setIsRegister(false);
    } catch (error) {
      let errorMessage = "Đăng ký thất bại. Vui lòng thử lại sau.";
      if (error.response && error.response.data) {
        // Xử lý nếu response.data là object chứa các lỗi validation
        if (
          typeof error.response.data === "object" &&
          !Array.isArray(error.response.data)
        ) {
          const errors = Object.values(error.response.data).join(", ");
          errorMessage = errors || errorMessage;
        } else {
          errorMessage = error.response.data || errorMessage;
        }
      }
      setSnackbarMessage(errorMessage);
      setSnackbarSeverity("error");
      setOpenSnackbar(true);
    } finally {
      setIsLoading(false);
    }
  };

  // Reset Password
  const handleResetPassword = async (e) => {
    e.preventDefault();

    if (!username) {
      setSnackbarMessage("Vui lòng nhập tên đăng nhập!");
      setSnackbarSeverity("error");
      setOpenSnackbar(true);
      return;
    }

    setIsLoading(true);
    try {
      await authAPI.forgotPassword(username);

      setSnackbarMessage("Mật khẩu tạm thời đã được gửi đến email của bạn.");
      setSnackbarSeverity("success");
      setOpenSnackbar(true);

      setShowResetPassword(false);
    } catch (error) {
      let errorMessage = "Không thể đặt lại mật khẩu. Vui lòng thử lại sau.";
      if (error.response && error.response.data) {
        errorMessage = error.response.data || errorMessage;
      }
      setSnackbarMessage(errorMessage);
      setSnackbarSeverity("error");
      setOpenSnackbar(true);
    } finally {
      setIsLoading(false);
    }
  };

  // Đóng snackbar
  const handleCloseSnackbar = (event, reason) => {
    if (reason === "clickaway") {
      return;
    }
    setOpenSnackbar(false);
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Grid container spacing={3}>
        <Grid item xs={12} md={6}>
          <Paper sx={{ p: 4, borderRadius: 2, boxShadow: 3 }}>
            <Box component="div" sx={{ mb: 3 }}>
              <Typography variant="h4" component="h1" gutterBottom>
                {showResetPassword
                  ? "Đặt lại mật khẩu"
                  : isRegister
                    ? "Đăng ký tài khoản nhân viên"
                    : "Đăng nhập nhân viên"}
              </Typography>
              <Typography variant="body1" color="text.secondary">
                {showResetPassword
                  ? "Nhập tên đăng nhập để nhận mật khẩu tạm thời"
                  : isRegister
                    ? "Tạo tài khoản nhân viên mới để quản lý hệ thống rạp chiếu phim"
                    : "Đăng nhập với tài khoản nhân viên để tiếp tục sử dụng hệ thống"}
              </Typography>
            </Box>

            {showResetPassword ? (
              // Form reset password
              <Box
                component="form"
                onSubmit={handleResetPassword}
                sx={{ mt: 1 }}
              >
                <TextField
                  margin="normal"
                  required
                  fullWidth
                  id="username"
                  label="Tên đăng nhập"
                  name="username"
                  autoComplete="username"
                  autoFocus
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  disabled={isLoading}
                />
                <Button
                  type="submit"
                  fullWidth
                  variant="contained"
                  sx={{ mt: 3, mb: 2 }}
                  disabled={isLoading}
                >
                  {isLoading ? (
                    <CircularProgress size={24} color="inherit" />
                  ) : (
                    "Gửi yêu cầu đặt lại mật khẩu"
                  )}
                </Button>
                <Button
                  fullWidth
                  variant="outlined"
                  color="secondary"
                  onClick={() => setShowResetPassword(false)}
                  sx={{ mb: 2 }}
                  disabled={isLoading}
                >
                  Quay lại đăng nhập
                </Button>
              </Box>
            ) : isRegister ? (
              // Form đăng ký
              <Box component="form" onSubmit={handleRegister} sx={{ mt: 1 }}>
                <TextField
                  margin="normal"
                  required
                  fullWidth
                  id="username"
                  label="Tên đăng nhập"
                  name="username"
                  autoComplete="username"
                  autoFocus
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  disabled={isLoading}
                />
                <TextField
                  margin="normal"
                  required
                  fullWidth
                  name="password"
                  label="Mật khẩu"
                  type="password"
                  id="password"
                  autoComplete="new-password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  disabled={isLoading}
                />
                <TextField
                  margin="normal"
                  required
                  fullWidth
                  id="email"
                  label="Email"
                  name="email"
                  autoComplete="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  disabled={isLoading}
                />
                <Button
                  type="submit"
                  fullWidth
                  variant="contained"
                  sx={{ mt: 3, mb: 2 }}
                  disabled={isLoading}
                >
                  {isLoading ? (
                    <CircularProgress size={24} color="inherit" />
                  ) : (
                    "Đăng ký"
                  )}
                </Button>
                <Button
                  fullWidth
                  variant="outlined"
                  color="secondary"
                  onClick={() => setIsRegister(false)}
                  sx={{ mb: 2 }}
                  disabled={isLoading}
                >
                  Đã có tài khoản? Đăng nhập
                </Button>
              </Box>
            ) : (
              // Form đăng nhập
              <Box component="form" onSubmit={handleLogin} sx={{ mt: 1 }}>
                <TextField
                  margin="normal"
                  required
                  fullWidth
                  id="username"
                  label="Tên đăng nhập"
                  name="username"
                  autoComplete="username"
                  autoFocus
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  disabled={isLoading}
                />
                <TextField
                  margin="normal"
                  required
                  fullWidth
                  name="password"
                  label="Mật khẩu"
                  type="password"
                  id="password"
                  autoComplete="current-password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  disabled={isLoading}
                />
                <Button
                  type="submit"
                  fullWidth
                  variant="contained"
                  sx={{ mt: 3, mb: 2 }}
                  disabled={isLoading}
                >
                  {isLoading ? (
                    <CircularProgress size={24} color="inherit" />
                  ) : (
                    "Đăng nhập"
                  )}
                </Button>
                <Grid container>
                  <Grid item xs>
                    <Button
                      variant="text"
                      onClick={() => setShowResetPassword(true)}
                      sx={{ textTransform: "none" }}
                      disabled={isLoading}
                    >
                      Quên mật khẩu?
                    </Button>
                  </Grid>
                  <Grid item>
                    <Button
                      variant="text"
                      onClick={() => setIsRegister(true)}
                      sx={{ textTransform: "none" }}
                      disabled={isLoading}
                    >
                      Chưa có tài khoản? Đăng ký
                    </Button>
                  </Grid>
                </Grid>
              </Box>
            )}
          </Paper>
        </Grid>

        <Grid item xs={12} md={6}>
          <Box
            sx={{
              height: "100%",
              display: "flex",
              flexDirection: "column",
              justifyContent: "center",
              p: 4,
              backgroundColor: "primary.main",
              color: "white",
              borderRadius: 2,
            }}
          >
            <Typography variant="h3" component="h2" gutterBottom>
              Hệ thống quản lý rạp chiếu phim
            </Typography>
            <Typography variant="h5" gutterBottom>
              Dành cho nhân viên và quản lý
            </Typography>
            <Typography variant="body1" paragraph>
              Nền tảng quản lý hiện đại giúp bạn dễ dàng điều hành rạp chiếu
              phim một cách hiệu quả.
            </Typography>
            <Typography variant="body1">
              Quản lý phim, rạp chiếu, phòng chiếu, lịch chiếu và nhiều tính
              năng khác.
            </Typography>
          </Box>
        </Grid>
      </Grid>

      <Snackbar
        open={openSnackbar}
        autoHideDuration={6000}
        onClose={handleCloseSnackbar}
      >
        <Alert
          onClose={handleCloseSnackbar}
          severity={snackbarSeverity}
          sx={{ width: "100%" }}
        >
          {snackbarMessage}
        </Alert>
      </Snackbar>
    </Container>
  );
}
