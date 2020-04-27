package ru.doc.converter.controller;

import org.apache.commons.lang3.StringUtils;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.document.DocumentFormat;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.local.JodConverter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping(value = "/api/convert-controller")
@Api("Conversion Operations which emulate a LibreOffice Online server conversion capabilities.")
public class ConvertController {

  private final OfficeManager officeManager;

  @ApiOperation(
      "Converts the incoming document to the specified format (provided as request param)"
          + " and returns the converted document.")
  @PostMapping(value = "/convert/to/{format}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public Object convertDocumentToHttp(
      @ApiParam(value = "The input document to convert.", required = true)
      @RequestParam(value = "data") final MultipartFile file,
      @ApiParam(value = "The document format to convert the input document to.", required = true)
      @PathVariable(value = "format") final String format
  ) {
    return convert(file, format);
  }

  private ResponseEntity<Object> convert(
      final MultipartFile file,
      final String format
  ) {
    if (file.isEmpty() || StringUtils.isBlank(format)) {
      return ResponseEntity.badRequest().build();
    }
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      final DocumentFormat outputFormat = DefaultDocumentFormatRegistry.getFormatByExtension(format);
      Assert.notNull(outputFormat, "Указанный формат не может быть null.");

      JodConverter
          .convert(file.getInputStream())
          .to(baos)
          .as(outputFormat)
          .execute();
      final HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.parseMediaType(outputFormat.getMediaType()));
      headers.add(
          "Content-disposition",
          "attachment; filename="
              + file.getOriginalFilename()
              + "."
              + outputFormat.getExtension()
      );
      return ResponseEntity.ok().headers(headers).body(baos.toByteArray());
    } catch (OfficeException | IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
    }
  }
}
