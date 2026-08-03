package com.bitan.village.inquiry;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record InquiryRequest(
        @NotBlank(message = "请填写称呼")
        @Size(max = 40, message = "称呼不能超过40个字")
        String name,

        @NotBlank(message = "请填写联系邮箱")
        @Email(message = "邮箱格式不正确")
        @Size(max = 120, message = "邮箱不能超过120个字符")
        String email,

        @FutureOrPresent(message = "到访日期不能早于今天")
        LocalDate visitDate,

        @Min(value = 1, message = "同行人数至少为1人")
        @Max(value = 30, message = "30人以上请通过当地接待渠道联系")
        Integer partySize,

        @NotBlank(message = "请写下你的问题或留言")
        @Size(max = 1000, message = "留言不能超过1000个字")
        String message
) {}
