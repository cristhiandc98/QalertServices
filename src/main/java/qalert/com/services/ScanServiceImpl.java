package qalert.com.services;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import qalert.com.interfaces.scan.IScanDao;
import qalert.com.interfaces.scan.IScanService;
import qalert.com.models.generic.Response2;
import qalert.com.models.scan.ScanHeaderResponse;
import qalert.com.models.scan.ScanRequest;
import qalert.com.models.scan.ScanResponse;
import qalert.com.utils.consts.EnvironmentConst;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.Block;
import software.amazon.awssdk.services.textract.model.DetectDocumentTextRequest;
import software.amazon.awssdk.services.textract.model.DetectDocumentTextResponse;
import software.amazon.awssdk.services.textract.model.Document;
import software.amazon.awssdk.services.textract.model.TextractException;

@Service
public class ScanServiceImpl implements IScanService{
    
	@Autowired
	private Environment env;

    @Autowired
    private IScanDao scanDao;


    @Override
    public Response2<ScanResponse> insertAndGetAdditivesFromPlainText(int profileId, String data) {
		Response2<ScanResponse> rsp = scanDao.insertAndGetAdditivesFromPlainText(profileId, data);

		setPercentageOfTotal(rsp);

        return rsp;
    }

    @Override
    public Response2<String> getAdditivesFromImage(MultipartFile file) {

		Response2<String> out;

		try {
			Response2<List<Block>> scanRsp = scanImage(file);
			
			if(scanRsp.isStatus()) {

	            List<Block> wordList = scanRsp.getData();
	            StringBuilder builder = new StringBuilder();
	            String text;
	            
	            for (Block palabra : wordList) {
	            	text = palabra.text();
	            	
	            	if(text != null) {		            
	            		text = text.toUpperCase()
	            				// .replaceAll("[()]", "")
	            				// .replaceAll(":|( Y )", ",")
	            				// .replaceAll(", ", ",")
	            				.replaceAll("Á", "A")
	            				.replaceAll("É", "E")
	            				.replaceAll("Í", "I")
	            				.replaceAll("Ó", "O")
	            				.replaceAll("Ú", "U")
	            				.trim();

	            		builder.append(text).append(" ");
	            	}
	            }

	            text = builder.toString();
	            
	            text = noRepeat(text);    

				if(text.isEmpty())
					out = new Response2<>(HttpStatus.BAD_REQUEST, "La imagen no contiene ingredientes.");
	            else if(text.length() <= 2000) 
		            out = new Response2<>(text);
	            else
                    out = new Response2<>(HttpStatus.BAD_REQUEST, "Ingredientes inválidos, favor de recortar la imagen.");
	            
			}else
                out = new Response2<>(scanRsp);

        } catch (TextractException ex) {
            out = new Response2<>(ex);
        }     

		return out;
	}

    private Response2<List<Block>> scanImage(MultipartFile file) {
		
		TextractClient awsTextExtract = null;
		Response2<List<Block>> out = null;
		
		try {
            SdkBytes sourceBytes = SdkBytes.fromInputStream(file.getInputStream());

            Document myDoc = Document.builder()
                    .bytes(sourceBytes)
                    .build();

            DetectDocumentTextRequest detectDocumentTextRequest = DetectDocumentTextRequest.builder()
                    .document(myDoc)
                    .build();
            
            awsTextExtract = createTextExtractObject();
            
            DetectDocumentTextResponse textResponse = awsTextExtract.detectDocumentText(detectDocumentTextRequest);

            out = new Response2<>(textResponse.blocks());
            
		} catch (TextractException | IOException ex) {
            out = new Response2<>(ex);
        }   
		finally {
			if(awsTextExtract != null) 
                awsTextExtract.close();
		}

		return out;
	}

    private TextractClient createTextExtractObject() {
		AwsCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(
                AwsBasicCredentials.create(
                		env.getRequiredProperty(EnvironmentConst.AWS_TEXTEXTRACT_ACCESS_KEY)
                		, env.getRequiredProperty(EnvironmentConst.AWS_TEXTEXTRACT_SECRET_KEY)
                		)
        );
		
        return TextractClient.builder()
                .region(Region.US_EAST_2)
                .credentialsProvider(credentialsProvider)
                .build();
	}

    private String noRepeat(String text) {
		try {
			return text.substring(0, text.length() / 2);
		} catch (Exception ex) {
			return text;	
		}
	}

    @Override
    public Response2<Boolean> insert(ScanRequest request) {
        return scanDao.insert(request);
    }

    @Override
    public Response2<ScanResponse> getAdditivesReport(ScanRequest request) {
		Response2<ScanResponse> rsp = scanDao.getAdditivesReport(request);

		setPercentageOfTotal(rsp);

        return rsp;
    }

	private void setPercentageOfTotal(Response2<ScanResponse> rsp){
		if(rsp.isStatus()){
			
			double total = 0;
	
			for (ScanHeaderResponse item : rsp.getData().getHeaderList()) 
				total += item.getTotal();
	
			if(total > 0){
				for (ScanHeaderResponse item : rsp.getData().getHeaderList()) 
					item.setPercentageOfTotal(item.getTotal() / total);
			}else{
				for (ScanHeaderResponse item : rsp.getData().getHeaderList()) 
					item.setPercentageOfTotal(0.0);
			}
		}
	}

	@Override
	public Response2<List<ScanHeaderResponse>> getScanList(int profileId) {
		return scanDao.getScanList(profileId);
	}
}
