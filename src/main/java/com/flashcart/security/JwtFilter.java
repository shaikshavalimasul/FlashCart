package com.flashcart.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//HTTP Request arrives
//        ↓
//FILE: JwtFilter.java ← HERE
//        ↓ (if token valid)
//DispatcherServlet
//        ↓
//FILE: ProductController.java / UserController.java
//        ↓
//FILE: ProductService.java / UserService.java
//        ↓
//FILE: ProductRepository.java / UserRepository.java
//        ↓
//PostgreSQL Database


@Component
public class JwtFilter extends OncePerRequestFilter
//    OncePerRequestFilter
//→ A Spring class we EXTEND (inherit from)
//        → "Once per request" = runs exactly ONCE
//  for each HTTP request
//→ Without this: filter might run multiple times
//  for one request (Spring's internal redirects)
//        → OncePerRequestFilter guarantees single execution
//
//        extends (not implements)
//→ OncePerRequestFilter is an abstract CLASS
//→ Classes extend abstract classes
//→ Interfaces use implements
//        → Remember from OOP: abstract class = extends
{

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

//    @Autowired JwtUtil jwtUtil
//→ Spring injects our JwtUtil bean
//→ FILE: JwtUtil.java
//→ We need it to validate tokens
//→ We need it to extract email from token
//
//    @Autowired UserDetailsService userDetailsService
//→ Spring injects UserService
//→ Remember: UserService implements UserDetailsService
//→ We need it to load user from database
//→ After extracting email from token:
//        "Load user details for this email"



    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException

//    @Override
//→ We are implementing the abstract method
//    from OncePerRequestFilter
//→ Spring calls this for every request
//
//    doFilterInternal
//→ The method name Spring looks for
//        → "Do the internal filter logic"
//
//    HttpServletRequest request
//→ The incoming HTTP request object
//→ Contains: URL, headers, body, method
//→ We read the Authorization header from this
//
//    HttpServletResponse response
//→ The outgoing HTTP response object
//→ If we detect invalid token:
//    we write 401 to this directly
//
//    FilterChain filterChain
//→ The chain of remaining filters
//→ If token is valid:
//        filterChain.doFilter(request, response)
//        = "pass request to next filter/controller"
//
//        throws ServletException, IOException
//→ These are checked exceptions
//→ Java forces us to declare them
//→ Spring handles them if thrown

    {
        String authHeader=request.getHeader("Authorization");

//        request.getHeader("Authorization")
//→ Reads the Authorization header from request
//
//        When user sends request with token:
//        Authorization: Bearer eyJhbGci...
//                ↑
//        This entire string is the header value
//
//            authHeader = "Bearer eyJhbGci..."
//
//        If no Authorization header:
//        authHeader = null
//        (user not logged in)

        String token=null;
        String email=null;
//        These will be filled IF token exists.
//            If no Authorization header → stay null.
//            Later we check: if token != null
//        This null check drives the entire logic.

        if(authHeader!=null && authHeader.startsWith("Bearer "))
        {
            token=authHeader.substring(7);
            email=jwtUtil.extractEmail(token);
        }
//        authHeader != null
//→ Did the request include Authorization header?
//→ If null → no token → skip extraction
//
//        authHeader.startsWith("Bearer ")
//→ Authorization header format is:
//        "Bearer eyJhbGci..."
//→ "Bearer " is a standard prefix
//→ Tells server this is a JWT token
//→ (vs "Basic " for username:password)
//→ We check prefix to confirm correct format
//
//            token = authHeader.substring(7)
//→ "Bearer " is 7 characters (B-e-a-r-e-r-space)
//→ substring(7) removes those 7 characters
//→ What remains: "eyJhbGci..."
//→ This is the actual JWT token
//
//        email = jwtUtil.extractEmail(token)
//→ FILE: JwtUtil.java
//→ Decodes the token payload
//→ Returns the email stored inside token:
//        "shaik@flashcart.com"
//→ Now we know WHO is making this request


        if(email!=null && SecurityContextHolder.getContext().getAuthentication()==null)
//            email != null
//→ We successfully extracted email from token
//→ Token exists and is decodable
//
//        SecurityContextHolder.getContext()
//                .getAuthentication() == null
//→ SecurityContextHolder = Spring Security's
//        storage for current request's authentication
//→ Like a temporary holder for "who is logged in"
//        for this specific request thread
//→ .getAuthentication() == null means:
//    "Not yet authenticated for this request"
//→ Why check this?
//            Prevent re-authenticating if already done
//        (could happen with filter chains)
        {

            UserDetails userDetails=userDetailsService.loadUserByUsername(email);
//            FILE: UserService.java
//            METHOD: loadUserByUsername(email)
//
//            "shaik@flashcart.com" → queries PostgreSQL
//→ Returns Spring Security's UserDetails
//            with: email, hashed password, role
//
//            Why load from database?
//→ We need to verify user still exists
//→ User could have been deleted after token issued
//→ Get fresh role (role might have changed)
//→ Spring Security needs UserDetails
//            to set authentication


            if(jwtUtil.validateToken(token,userDetails.getUsername()))
//                FILE: JwtUtil.java
//            METHOD: validateToken(token, email)
//
//            Checks:
//            1. Email in token matches email we loaded
//            2. Token has not expired
//
//            userDetails.getUsername()
//→ Returns the email (our "username")
//→ We set this in loadUserByUsername:
//  .withUsername(user.getEmail())
//
//            If validation passes → user is legitimate
//            If validation fails → skip authentication
//  → request will be rejected with 401 later
            {
                UsernamePasswordAuthenticationToken authToken=new UsernamePasswordAuthenticationToken(
                        userDetails,null,userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource()
                        .buildDetails(request));

                SecurityContextHolder.getContext().
                        setAuthentication(authToken);

//                UsernamePasswordAuthenticationToken
//→ Spring Security's way of representing
//                a successfully authenticated user
//→ Contains: who they are + what they can do
//
//                new UsernamePasswordAuthenticationToken(
//                        userDetails,  ← the authenticated user
//                null,         ← credentials (password)
//                null because already verified
//                userDetails.getAuthorities()) ← roles/permissions
//→ Creates authentication object
//
//                authToken.setDetails(...)
//→ Adds request-specific details
//→ IP address, session ID etc.
//→ Used for audit logging
//
//                SecurityContextHolder.getContext()
//                        .setAuthentication(authToken)
//→ SecurityContextHolder = thread-local storage
//→ Stores authentication for this request
//→ Now Spring Security knows:
//                "This request is from shaik@flashcart.com
//                with USER role"
//→ Authorization checks can use this
//→ Cleared automatically after request completes
            }
        }
   filterChain.doFilter(request,response);
    }

}

//
//REQUEST: GET /api/orders/my
//Headers: Authorization: Bearer eyJhbGci...
//
//FILE: JwtFilter.java runs
//        ↓
//Extract header: "Bearer eyJhbGci..."
//        ↓
//Remove "Bearer ": "eyJhbGci..."
//        ↓
//FILE: JwtUtil.java
//extractEmail("eyJhbGci...")
//→ Decode payload
//→ Returns "shaik@flashcart.com"
//        ↓
//FILE: UserService.java
//loadUserByUsername("shaik@flashcart.com")
//→ Query PostgreSQL users table
//→ Returns UserDetails (email, password, role)
//        ↓
//FILE: JwtUtil.java
//validateToken(token, "shaik@flashcart.com")
//→ Recalculate signature
//→ Check expiry
//→ Returns true
//        ↓
//Create UsernamePasswordAuthenticationToken
//Store in SecurityContextHolder
//"This request = shaik@flashcart.com, role=USER"
//        ↓
//        filterChain.doFilter() → continue to Controller
//        ↓
//FILE: OrderController.java
//Spring Security checks: "USER role? YES"
//        → Method runs
//→ Returns orders





//
//❓ Interview Questions — Topic 33
//
//Q1. What is a Filter in Spring Security?
//Answer: A class that intercepts every HTTP request before it reaches the Controller. Filters form a chain — each performs a specific security check. JwtFilter extracts and validates JWT tokens, setting authentication context if token is valid.
//
//Q2. What is OncePerRequestFilter?
//Answer: An abstract Spring class that guarantees a filter runs exactly once per request regardless of internal Spring dispatching. We extend it for JwtFilter to prevent duplicate token validation on the same request.
//
//Q3. What is SecurityContextHolder?
//Answer: Thread-local storage that holds the authentication information for the current request. JwtFilter stores the authenticated user here after validating the JWT. Spring Security reads from here for authorization decisions. Automatically cleared after request completes.
//
//Q4. What does filterChain.doFilter() do?
//Answer: Passes the request to the next filter in the chain or to the DispatcherServlet if no more filters. Must always be called — including for unauthenticated requests — because public endpoints should pass through without tokens.