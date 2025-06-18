import React, { useState, useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Link, useNavigate, useLocation } from "react-router-dom";
import { loginUser } from "../../redux/actions/authActions";
import { FcGoogle } from "react-icons/fc";
import { oauth2LoginSuccess } from "../../redux/actions/authActions";
import { fetchUserData } from "../../redux/service/authService";
import types from "../../redux/types";

const SignIn = () => {
  const [phoneNumber, setPhoneNumber] = useState("");
  const [password, setPassword] = useState("");
  const [errPhoneNumber, setErrPhoneNumber] = useState("");
  const [errPassword, setErrPassword] = useState("");
  const [successMsg, setSuccessMsg] = useState("");
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const location = useLocation();
  const error = useSelector((state) => state.auth.error);
  const auth = useSelector(state => state.auth.auth);
  const isAuthenticated = useSelector((state) => state.auth.isAuthenticated);

  // Lấy thông tin từ state navigation
  const from = location.state?.from || "/";
  const message = location.state?.message || "";

  useEffect(() => {
    if (isAuthenticated) {
      const isAdmin = auth?.role?.name === "ROLE_ADMIN";
      if (isAdmin) {
        navigate("/admin/dashboard");
      } else {
        // Redirect về trang trước đó nếu có, hoặc về trang chủ
        navigate(from);
      }
    }
  }, [isAuthenticated, auth, navigate, from]);

  useEffect(() => {
    dispatch({ type: types.LOGIN_RESET });
  }, [dispatch]);

  const handlePhoneNumber = (e) => {
    setPhoneNumber(e.target.value);
    setErrPhoneNumber("");
  };

  const handlePassword = (e) => {
    setPassword(e.target.value);
    setErrPassword("");
  };

  const handleSignIn = (e) => {
    e.preventDefault();

    if (!phoneNumber) {
      setErrPhoneNumber("Vui lòng nhập số điện thoại");
    }

    if (!password) {
      setErrPassword("Vui lòng nhập mật khẩu");
    }

    if (phoneNumber && password) {
      dispatch(loginUser(phoneNumber, password));
    }
  };

  const handleGoogleLogin = () => {
    const width = 500;
    const height = 600;
    const left = window.screenX + (window.innerWidth - width) / 2;
    const top = window.screenY + (window.innerHeight - height) / 2;

    window.open(
        "https://www.capstone.io.vn/api/oauth2/authorization/google",
        "_blank",
      `width=${width},height=${height},top=${top},left=${left}`
    );

    const messageListener = async (event) => {
      if (event.origin !== "https://www.capstone.io.vn") return;
      const { token } = event.data;

      if (token) {
        try {
          localStorage.setItem("token", token);
          const data = await fetchUserData(token);
          if (data && data.data) {
            const { id, email, fullName, phoneNumber, address, avatar, role } = data.data;
            const user = { id, email, fullName, phoneNumber, address, avatar, role };
            const isAdmin = role?.name === "ROLE_ADMIN";

            dispatch(oauth2LoginSuccess(user, token));
            navigate(isAdmin ? "/admin/dashboard" : from);
          } else {
            console.error("Invalid user data:", data);
            navigate("/signin");
          }
        } catch (error) {
          console.error("OAuth2 fetch error:", error);
          navigate("/signin");
        }
      }
      window.removeEventListener("message", messageListener);
    };

    window.addEventListener("message", messageListener);
  };

  return (
    <div className="w-full min-h-screen bg-[#f5f5f5] flex items-center justify-center py-10">
      <div className="w-[400px] bg-white rounded-sm shadow-sm">
        {/* Header */}
        <div className="bg-[#ee4d2d] p-4">
          <h2 className="text-white text-xl font-medium text-center">Đăng Nhập</h2>
        </div>

        {/* Content */}
        <div className="p-8">
          {/* Hiển thị thông báo từ navigation state */}
          {message && (
            <div className="mb-4 p-3 bg-blue-50 border border-blue-200 rounded-sm">
              <p className="text-blue-700 text-sm text-center">
                {message}
              </p>
            </div>
          )}

          {successMsg ? (
            <div className="flex flex-col items-center">
              <p className="text-green-500 font-medium text-center mb-4">
                {successMsg}
              </p>
              <Link to="/signup" className="w-full">
                <button className="w-full h-10 bg-[#ee4d2d] text-white rounded-sm hover:bg-[#f05d40] transition duration-300">
                  Đăng Ký
                </button>
              </Link>
            </div>
          ) : (
            <form onSubmit={handleSignIn} className="flex flex-col gap-4">
              {/* Phone Number */}
              <div className="flex flex-col gap-1">
                <input
                  onChange={handlePhoneNumber}
                  value={phoneNumber}
                  className="w-full h-10 px-3 border border-gray-300 rounded-sm outline-none focus:border-[#ee4d2d]"
                  type="tel"
                  placeholder="Số điện thoại"
                />
                {errPhoneNumber && (
                  <p className="text-[#ee4d2d] text-sm">
                    {errPhoneNumber}
                  </p>
                )}
              </div>

              {/* Password */}
              <div className="flex flex-col gap-1">
                <input
                  onChange={handlePassword}
                  value={password}
                  className="w-full h-10 px-3 border border-gray-300 rounded-sm outline-none focus:border-[#ee4d2d]"
                  type="password"
                  placeholder="Mật khẩu"
                />
                {errPassword && (
                  <p className="text-[#ee4d2d] text-sm">
                    {errPassword}
                  </p>
                )}
                <Link to="/forgot-password" className="text-[#ee4d2d] text-sm hover:underline text-right">
                  Quên mật khẩu?
                </Link>
              </div>

              <button
                type="submit"
                className="w-full h-10 bg-[#ee4d2d] text-white rounded-sm hover:bg-[#f05d40] transition duration-300"
              >
                Đăng Nhập
              </button>

              <div className="relative flex items-center justify-center my-2">
                <div className="border-t border-gray-300 w-full"></div>
                <span className="bg-white px-2 text-sm text-gray-500 absolute">HOẶC</span>
              </div>

              <button
                type="button"
                onClick={handleGoogleLogin}
                className="w-full h-10 border border-gray-300 rounded-sm hover:bg-gray-50 transition duration-300 flex items-center justify-center gap-2"
              >
                <FcGoogle className="text-xl" />
                <span className="text-gray-700">Đăng nhập với Google</span>
              </button>

              <div className="flex items-center justify-center gap-2 text-sm mt-4">
                <span className="text-gray-500">Bạn chưa có tài khoản?</span>
                <Link to="/signup" className="text-[#ee4d2d] hover:underline">
                  Đăng ký
                </Link>
              </div>
            </form>
          )}

          {!isAuthenticated && error && (
            <p className="text-[#ee4d2d] text-sm text-center mt-4">
              {error}
            </p>
          )}
        </div>
      </div>
    </div>
  );
};

export default SignIn;