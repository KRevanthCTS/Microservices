import React, { useEffect, useRef } from 'react'
import { Link } from 'react-router-dom'
import logo from '../assets/Logo.svg'
import Image from '../assets/Image.png'

export default function Landing(){
  /* Intersection Observer for scroll-reveal animations */
  const aboutRef = useRef(null)
  useEffect(() => {
    const observer = new IntersectionObserver(
      entries => entries.forEach(e => {
        if (e.isIntersecting) e.target.classList.add('visible')
      }),
      { threshold: 0.15 }
    )
    const els = aboutRef.current?.querySelectorAll('.reveal')
    els?.forEach(el => observer.observe(el))
    return () => els?.forEach(el => observer.unobserve(el))
  }, [])

  return (
    <>
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

    {/* ── About Us Section ── */}
    <section className="about-section" ref={aboutRef}>
      <div className="about-inner">

        {/* Section header */}
        <div className="about-header reveal">
          <span className="about-badge">About Us</span>
          <h2 className="about-title">Why Reward360?</h2>
          <p className="about-subtitle">
            We help businesses turn every transaction into a meaningful connection.
            Our platform combines cutting-edge technology with proven engagement strategies.
          </p>
        </div>

        {/* Feature cards */}
        <div className="about-features">
          <div className="about-card reveal">
            <div className="about-card-icon">🎯</div>
            <h3>Smart Targeting</h3>
            <p>AI-powered customer segmentation that delivers the right offer to the right person at the right time.</p>
          </div>
          <div className="about-card reveal">
            <div className="about-card-icon">🔄</div>
            <h3>Seamless Integration</h3>
            <p>Connect with your existing POS, CRM, and e-commerce systems in minutes — not months.</p>
          </div>
          <div className="about-card reveal">
            <div className="about-card-icon">📈</div>
            <h3>Real-time Analytics</h3>
            <p>Track engagement, redemption rates, and ROI with dashboards that update in real time.</p>
          </div>
          <div className="about-card reveal">
            <div className="about-card-icon">🛡️</div>
            <h3>Fraud Protection</h3>
            <p>Built-in anomaly detection flags suspicious activity before it impacts your bottom line.</p>
          </div>
          <div className="about-card reveal">
            <div className="about-card-icon">🏆</div>
            <h3>Tiered Rewards</h3>
            <p>Silver, Gold, Platinum — create multi-tier programs that motivate customers to level up.</p>
          </div>
          <div className="about-card reveal">
            <div className="about-card-icon">🌐</div>
            <h3>Omnichannel</h3>
            <p>Reward customers across web, mobile, and in-store — one unified experience everywhere.</p>
          </div>
        </div>

        {/* Mission strip */}
        <div className="about-mission reveal">
          <div className="mission-content">
            <h3>Our Mission</h3>
            <p>
              At Reward360, we believe loyalty is earned — not bought. Our mission is to empower businesses
              of every size to build genuine, lasting relationships with their customers through
              intelligent, transparent, and rewarding engagement programs.
            </p>
          </div>
        </div>

        {/* Stats row */}
        <div className="about-stats reveal">
          <div className="about-stat">
            <span className="stat-num">10K+</span>
            <span className="stat-label">Active Users</span>
          </div>
          <div className="about-stat">
            <span className="stat-num">2M+</span>
            <span className="stat-label">Points Redeemed</span>
          </div>
          <div className="about-stat">
            <span className="stat-num">500+</span>
            <span className="stat-label">Reward Offers</span>
          </div>
          <div className="about-stat">
            <span className="stat-num">99.9%</span>
            <span className="stat-label">Uptime</span>
          </div>
        </div>

      </div>
    </section>
    </>
  )
}
