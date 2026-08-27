package com.example.demo.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class UploadExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSize(
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute(
                "errorMessage",
                "画像は2MB以内を選択してください");

        String requestUri = request.getRequestURI();

        if (requestUri.startsWith("/profile/")) {

            return "redirect:/profile";
        }

        if (requestUri.equals("/accounts/create")) {

            return "redirect:/accounts/create";
        }

        if (requestUri.startsWith("/accounts/update/")) {

            return "redirect:"
                    + requestUri.replace(
                            "/accounts/update/",
                            "/accounts/edit/");
        }

        return "redirect:/login";
    }
}
