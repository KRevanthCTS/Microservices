import React from 'react'
import { Link } from 'react-router-dom'
import logo from '../assets/Logo.svg'
import Image from '../assets/Image.png'

export default function Landing(){
  return (
    <div className="landing-hero">
      {/* Floating background particles */}
      <div className="landing-particles">
        <span className="particle p1" />
        <span className="particle p2" />
        <span className="particle p3" />
        <span className="particle p4" />
        <span className="particle p5" />
      </div>

      <div className="landing-inner">
        <div className="left fade-in-up">
          <h1 className="hero-title">
            <span className="word-reveal d1">Engage</span>{' '}
            <span className="word-reveal d2">your</span><br/>
            <span className="word-reveal d3">customers</span>{' '}
            <span className="word-reveal d4">and</span>{' '}
            <span className="word-reveal d5">build</span><br/>
            <span className="word-reveal d6">lifelong</span>{' '}
            <span className="word-reveal d7">customer</span><br/>
            <span className="word-reveal d8">loyalty</span>
          </h1>
          <p className="lead fade-in-up delay-1">A data-driven customer engagement ecosystem to help you run your most ambitious reward program</p>
          <div className="cta-row fade-in-up delay-2">
            <Link to="/register" className="button cta-glow">Get Started</Link>
            <Link to="/login" className="link">Already have an account?</Link>
          </div>

          {/* Trust badges */}
          <div className="trust-badges fade-in-up delay-3">
            <div className="trust-badge slide-in-left">
              <span className="trust-icon">🔒</span>
              <span>Secure</span>
            </div>
            <div className="trust-badge slide-in-left d2">
              <span className="trust-icon">⚡</span>
              <span>Real-time</span>
            </div>
            <div className="trust-badge slide-in-left d3">
              <span className="trust-icon">📊</span>
              <span>Analytics</span>
            </div>
          </div>
        </div>

        <div className="right fade-in-up delay-1">
          <div
            className="graphic graphic-float"
            style={{
              backgroundImage: `url(${Image})`,
              backgroundSize: 'cover',
              backgroundPosition: 'center'
            }}
          >
            <img src={logo} alt="logo" className="landing-logo logo-spin" />
            <div className="pulse" />
            <div className="pulse pulse-2" />
            <div className="orbit-ring">
              <span className="orbit-dot" />
            </div>

            {/* Stat cards floating around graphic */}
            <div className="float-card fc-1 pop-in">
              <span className="fc-num counter">10K+</span>
              <span className="fc-label">Users</span>
            </div>
            <div className="float-card fc-2 pop-in d2">
              <span className="fc-num counter">98%</span>
              <span className="fc-label">Satisfaction</span>
            </div>
            <div className="float-card fc-3 pop-in d3">
              <span className="fc-num counter">500+</span>
              <span className="fc-label">Rewards</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
