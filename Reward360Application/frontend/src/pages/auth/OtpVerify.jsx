import React, { useState, useEffect, useRef } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import api from '../../api/client'

export default function OtpVerify() {
  const navigate = useNavigate()
  const resetEmail = sessionStorage.getItem('resetEmail') || ''
  const mockOtp = sessionStorage.getItem('mockOtp') || ''

  // ── Step state: 'otp' → 'reset' → 'done'
  const [step, setStep] = useState('otp')

  // ── OTP state
  const [otp, setOtp] = useState(['', '', '', '', '', ''])
  const [otpErr, setOtpErr] = useState('')
  const [verifying, setVerifying] = useState(false)
  const inputRefs = useRef([])

  // ── Password state
  const [newPass, setNewPass] = useState('')
  const [confirmPass, setConfirmPass] = useState('')
  const [showNewPass, setShowNewPass] = useState(false)
  const [showConfirmPass, setShowConfirmPass] = useState(false)
  const [passErr, setPassErr] = useState('')
  const [resetting, setResetting] = useState(false)
  const [showPopup, setShowPopup] = useState(false)

  // Redirect if no email in session (user came directly to /otp)
  useEffect(() => {
    if (!resetEmail && !showPopup) {
      navigate('/forgot')
    }
  }, [resetEmail, showPopup, navigate])

  // ── OTP input handlers
  const handleOtpChange = (index, value) => {
    if (!/^\d*$/.test(value)) return // digits only
    const newOtp = [...otp]
    newOtp[index] = value.slice(-1) // single digit
    setOtp(newOtp)
    setOtpErr('')
    // Auto-focus next
    if (value && index < 5) {
      inputRefs.current[index + 1]?.focus()
    }
  }

  const handleOtpKeyDown = (index, e) => {
    if (e.key === 'Backspace' && !otp[index] && index > 0) {
      inputRefs.current[index - 1]?.focus()
    }
  }

  const handleOtpPaste = (e) => {
    e.preventDefault()
    const pasted = e.clipboardData.getData('text').replace(/\D/g, '').slice(0, 6)
    if (pasted.length === 6) {
      setOtp(pasted.split(''))
      inputRefs.current[5]?.focus()
    }
  }

  // ── Verify OTP
  const verifyOtp = async (e) => {
    e.preventDefault()
    const entered = otp.join('')
    if (entered.length !== 6) {
      setOtpErr('Please enter all 6 digits')
      return
    }
    setVerifying(true)
    // Simulate verification delay
    await new Promise(r => setTimeout(r, 1200))
    setVerifying(false)

    if (entered !== mockOtp) {
      setOtpErr('❌ Invalid OTP. Please try again.')
      setOtp(['', '', '', '', '', ''])
      inputRefs.current[0]?.focus()
      return
    }
    // Success → move to password reset step
    setStep('reset')
  }

  // ── Reset password
  const handleResetPassword = async (e) => {
    e.preventDefault()
    setPassErr('')

    if (newPass.length < 8) {
      setPassErr('Password must be at least 8 characters')
      return
    }
    if (newPass !== confirmPass) {
      setPassErr('Passwords do not match')
      return
    }

    setResetting(true)
    try {
      // Call the real backend to reset the password
      await api.post('/auth/reset-password', {
        email: resetEmail,
        newPassword: newPass
      })
      setResetting(false)

      // Show success popup and redirect to login
      setShowPopup(true)
      setTimeout(() => {
        // Cleanup session only after popup is done
        sessionStorage.removeItem('resetEmail')
        sessionStorage.removeItem('mockOtp')
        setShowPopup(false)
        navigate('/login')
      }, 2500)
    } catch (err) {
      setResetting(false)
      const msg = err.response?.data?.error || 'Password reset failed. Please try again.'
      setPassErr(msg)
    }
  }

  // ── Password eye icon
  const EyeIcon = ({ show }) => show ? (
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none"><path d="M1.5 12s4-7.5 10.5-7.5S22.5 12 22.5 12s-4 7.5-10.5 7.5S1.5 12 1.5 12z" stroke="#0b5ed7" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/><circle cx="12" cy="12" r="3" stroke="#0b5ed7" strokeWidth="1.5"/></svg>
  ) : (
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none"><path d="M17.94 17.94A10.94 10.94 0 0 1 12 19.5C5.5 19.5 1.5 12 1.5 12c1.46-2.57 3.76-4.7 6.54-6.02" stroke="#0b5ed7" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/><path d="M3 3l18 18" stroke="#0b5ed7" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/><path d="M9.88 9.88A3 3 0 0 0 14.12 14.12" stroke="#0b5ed7" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/></svg>
  )

  return (
    <div className="auth-page auth-centered">
      <div className="auth-form-panel">
        <div className="auth-card fade-in-up">

          {/* ═══════ STEP 1: OTP ENTRY ═══════ */}
          {step === 'otp' && (
            <>
              <div className="auth-icon-circle">🔢</div>
              <h2 className="auth-heading">Enter Verification Code</h2>
              <p className="auth-subheading">
                We sent a 6-digit code to <strong>{resetEmail}</strong>
                <br />
                <span className="auth-mock-hint">🧪 Mock OTP: <code className="auth-otp-hint">{mockOtp}</code></span>
              </p>

              <form onSubmit={verifyOtp} className="auth-form">
                {/* OTP boxes */}
                <div className="auth-otp-boxes" onPaste={handleOtpPaste}>
                  {otp.map((digit, i) => (
                    <input
                      key={i}
                      ref={el => inputRefs.current[i] = el}
                      className={`auth-otp-input ${digit ? 'auth-otp-filled' : ''}`}
                      type="text"
                      inputMode="numeric"
                      maxLength={1}
                      value={digit}
                      onChange={e => handleOtpChange(i, e.target.value)}
                      onKeyDown={e => handleOtpKeyDown(i, e)}
                      autoFocus={i === 0}
                    />
                  ))}
                </div>

                {otpErr && <div className="auth-error">{otpErr}</div>}

                <button className="auth-submit" type="submit" disabled={verifying || otp.join('').length !== 6}>
                  {verifying ? (
                    <span className="auth-btn-loading"><span className="auth-spinner" /> Verifying…</span>
                  ) : '✅ Verify OTP'}
                </button>
              </form>

              <div className="auth-footer-links" style={{ justifyContent: 'center' }}>
                <Link className="link" to="/forgot">← Back to Forgot Password</Link>
              </div>
            </>
          )}

          {/* ═══════ STEP 2: RESET PASSWORD ═══════ */}
          {step === 'reset' && (
            <>
              <div className="auth-icon-circle auth-icon-success">✅</div>
              <h2 className="auth-heading">OTP Verified!</h2>
              <p className="auth-subheading">Now set your new password for <strong>{resetEmail}</strong></p>

              <form onSubmit={handleResetPassword} className="auth-form">
                {/* New Password */}
                <div className="auth-field">
                  <label className="auth-label">New Password</label>
                  <div className="auth-input-wrap">
                    <span className="auth-input-icon">🔒</span>
                    <input
                      className={`auth-input${showNewPass ? '' : ' auth-input-masked'}`}
                      type="text"
                      autoComplete="off"
                      placeholder="Min 8 characters"
                      value={newPass}
                      onChange={e => setNewPass(e.target.value)}
                      required
                      minLength={8}
                      autoFocus
                    />
                    <button type="button" className="auth-eye-btn" onClick={() => setShowNewPass(s => !s)}>
                      <EyeIcon show={showNewPass} />
                    </button>
                  </div>
                </div>

                {/* Confirm Password */}
                <div className="auth-field">
                  <label className="auth-label">Confirm Password</label>
                  <div className="auth-input-wrap">
                    <span className="auth-input-icon">🔒</span>
                    <input
                      className={`auth-input${showConfirmPass ? '' : ' auth-input-masked'}`}
                      type="text"
                      autoComplete="off"
                      placeholder="Re-enter your new password"
                      value={confirmPass}
                      onChange={e => setConfirmPass(e.target.value)}
                      required
                      minLength={8}
                    />
                    <button type="button" className="auth-eye-btn" onClick={() => setShowConfirmPass(s => !s)}>
                      <EyeIcon show={showConfirmPass} />
                    </button>
                  </div>
                </div>

                {passErr && <div className="auth-error">{passErr}</div>}

                <button className="auth-submit" type="submit" disabled={resetting}>
                  {resetting ? (
                    <span className="auth-btn-loading"><span className="auth-spinner" /> Resetting…</span>
                  ) : '🔐 Reset Password'}
                </button>
              </form>
            </>
          )}

          {/* ═══════ SUCCESS POPUP ═══════ */}
          {showPopup && (
            <div className="auth-popup-overlay">
              <div className="auth-popup-box fade-in-up">
                <div className="auth-popup-icon">🎉</div>
                <h3 className="auth-popup-title">Password Reset Successful!</h3>
                <p className="auth-popup-msg">Redirecting to login…</p>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}