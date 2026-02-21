import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'

export default function ForgotPassword() {
  const [email, setEmail] = useState('')
  const [err, setErr] = useState('')
  const [sending, setSending] = useState(false)
  const [sent, setSent] = useState(false)
  const [generatedOtp, setGeneratedOtp] = useState('')
  const navigate = useNavigate()

  // Generate a random 6-digit OTP
  const generateOtp = () => String(Math.floor(100000 + Math.random() * 900000))

  const handleSendOtp = async (e) => {
    e.preventDefault()
    setErr('')

    if (!email || !/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(email)) {
      setErr('Please enter a valid email address')
      return
    }

    setSending(true)
    // Simulate API delay
    await new Promise(r => setTimeout(r, 1500))
    setSending(false)
    setSent(true)

    // Generate OTP and store in sessionStorage so OtpVerify can access it
    const otp = generateOtp()
    setGeneratedOtp(otp)
    sessionStorage.setItem('resetEmail', email)
    sessionStorage.setItem('mockOtp', otp)
  }

  return (
    <div className="auth-page auth-centered">
      <div className="auth-form-panel">
        <div className="auth-card fade-in-up">

          {!sent ? (
            <>
              <div className="auth-icon-circle">🔑</div>
              <h2 className="auth-heading">Forgot Password?</h2>
              <p className="auth-subheading">
                No worries! Enter your registered email and we'll send you a one-time verification code.
              </p>

              <form onSubmit={handleSendOtp} className="auth-form">
                <div className="auth-field">
                  <label className="auth-label">Email Address</label>
                  <div className="auth-input-wrap">
                    <span className="auth-input-icon">✉️</span>
                    <input
                      className="auth-input"
                      type="email"
                      placeholder="you@example.com"
                      value={email}
                      onChange={e => setEmail(e.target.value)}
                      required
                      autoFocus
                    />
                  </div>
                </div>

                {err && <div className="auth-error">{err}</div>}

                <button className="auth-submit" type="submit" disabled={sending}>
                  {sending ? (
                    <span className="auth-btn-loading">
                      <span className="auth-spinner" /> Sending OTP…
                    </span>
                  ) : '📩 Send OTP'}
                </button>
              </form>

              <div className="auth-footer-links" style={{ justifyContent: 'center' }}>
                <span>Remember your password? <Link className="link" to="/login">Back to Login</Link></span>
              </div>
            </>
          ) : (
            <>
              <div className="auth-icon-circle auth-icon-success">✅</div>
              <h2 className="auth-heading">OTP Sent!</h2>
              <p className="auth-subheading">
                We've sent a 6-digit verification code to <strong>{email}</strong>.
                <br />
                <span className="auth-mock-hint">🧪 Mock OTP: <code className="auth-otp-hint">{generatedOtp}</code></span>
              </p>

              <button className="auth-submit" onClick={() => navigate('/otp')}>
                🔢 Enter OTP & Reset Password
              </button>

              <div className="auth-footer-links" style={{ justifyContent: 'center', flexDirection: 'column', gap: '8px', alignItems: 'center' }}>
                <span style={{ color: '#94a3b8', fontSize: '13px' }}>Didn't receive it?</span>
                <button
                  className="auth-link-btn"
                  onClick={() => { setSent(false); setSending(false) }}
                >
                  ← Re-enter email & resend
                </button>
              </div>
            </>
          )}
        </div>
      </div>
    </div>
  )
}
