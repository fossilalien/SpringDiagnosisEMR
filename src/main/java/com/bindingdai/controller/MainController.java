package com.bindingdai.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.bindingdai.repository.*;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import com.bindingdai.model.*;
import org.springframework.ui.Model;
import java.util.Date;
import java.text.*;
/**
 * Created by daibinding on 15/10/15.
 */
@Controller
public class MainController {

   /* @Autowired
    private ClinicRepository ClinicRepository;
    @Autowired
    private DepartmentRepository DepartmentRepository;

    @Autowired
    private InstrumentExamRepository instrumentExamRepository;

    @Autowired
    private PhysicalExamRepository physicalExamRepository;
  */

    @Autowired
    private DoctorRepository DoctorRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private SymptomRepository symptomRepository;
    @Autowired
    private DiagnosisRepository diagnosisRepository;
    @Autowired
    private PhysicalExamRepository physicalExamRepository;
    @Autowired
    private InstrumentRepository instrumentRepository;
    @Autowired
    private DrugRepository drugRepository;
    @Autowired
    private PatientRecordRepository patientRecordRepository;


    @RequestMapping(value="/",method = RequestMethod.GET)
    public String index()
    {
        return "index";
    }

    @RequestMapping(value="/test",method = RequestMethod.GET)
    public String test()
    {
        return "testjsp";
    }

    @RequestMapping(value="/order",method = RequestMethod.GET)
    public String order()
    {
        System.out.println("write_order_get到达");
        return "order";
    }

    @RequestMapping(value="/order",method = RequestMethod.POST)
    public String get_order(HttpServletResponse response,HttpServletRequest request)
    {
        String temp=request.toString();
        System.out.println("write_order_post到达");
        System.out.println(temp);
        return "order";
    }


    @RequestMapping(value="/write_order",method = RequestMethod.POST)
    public String write_order(HttpServletResponse response,HttpServletRequest request,Model model)throws Exception
    {
        List<PatientEntity> patientEntityList=patientRepository.findAll();
        JSONArray jsons=new JSONArray();
        String clinicid=request.getParameter("clinicid");

        for(PatientEntity patientkey:patientEntityList)
        {
            //int tempInt=Integer.parseInt(patientEntity.getPatientClinicId());
            //System.out.println(patientEntity.getPatientClinicId());
           if(patientkey.getPatientClinicId().trim().equals(clinicid.trim()))
           {

               System.out.println(patientkey.toString());
               System.out.println("成功找到开处方的人");

               JSONObject jsonObject=new JSONObject();
               jsonObject.put("id",patientkey.getIdpatient());
               jsonObject.put("name",patientkey.getPatientName());
               jsonObject.put("person_id",patientkey.getPatientPersonId());
               jsonObject.put("clinic_id",patientkey.getPatientClinicId());
               jsonObject.put("paycard_id",patientkey.getPatientPaycardId());
               jsonObject.put("age",patientkey.getPatientAge());
               jsonObject.put("telnumber",patientkey.getPatientTelNumber());
               jsonObject.put("gender",patientkey.getPatientGender());
               model.addAttribute("patientKey",jsonObject.toString());

               return "order";

           }
        }
        return "clinic";
    }

    @RequestMapping(value="/clinicDiagnosis",method = RequestMethod.POST)
    public  String login(String username,String password,String clinic)
    {
        //List<ClinicEntity> clinicEntityList=ClinicRepository.findAll();

        List<DoctorEntity> doctorEntityList=DoctorRepository.findAll();

        int flag=0;
        for(DoctorEntity doctor:doctorEntityList)
        {
            if(username.equals(doctor.getUsername()))
            {
                if(password.equals(doctor.getPassword()))
                {
                    System.out.println("kkk到达");

                    return "clinic";
                }
                flag=1;

            }
        }
        System.out.println(username);
        System.out.println(password);

        return "redirect:/";

    }

    @RequestMapping(value = "/clinicDiagnosis",method = RequestMethod.GET)
    public String clinicDiagnosis(ModelMap modelMap)
    {
        List<PatientEntity>   patientEntityList=patientRepository.findAll();
        for(PatientEntity patient:patientEntityList)
        {
            System.out.println(patient.getPatientClinicId());
            System.out.println(patient.getPatientClinicState());
        }
        modelMap.addAttribute("patientList",patientEntityList);
        return "clinic";
    }

    @RequestMapping("/updateWaiting"  )
    public void resolveJson(HttpServletRequest request, HttpServletResponse response) throws Exception  {

        List<PatientEntity>   patientEntityList=patientRepository.findAll();
        JSONArray jsons=new JSONArray();
        for(PatientEntity patientkey:patientEntityList)
        {
            if(patientkey.getPatientClinicState().equals("1"))
            {
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("id",patientkey.getIdpatient());
                jsonObject.put("name",patientkey.getPatientName());
                jsonObject.put("person_id",patientkey.getPatientPersonId());
                jsonObject.put("clinic_id",patientkey.getPatientClinicId());
                jsonObject.put("paycard_id",patientkey.getPatientPaycardId());
                jsonObject.put("age",patientkey.getPatientAge());
                jsonObject.put("telnumber",patientkey.getPatientTelNumber());
                jsonObject.put("gender",patientkey.getPatientGender());
                if(patientkey.getPatientPaytype()!=null)
                {
                    if(patientkey.getPatientPaytype().equals("public"))
                    {
                        jsonObject.put("paytype","公费");
                    }
                    else if(patientkey.getPatientPaytype().equals("private"))
                    {
                        jsonObject.put("paytype","自费");
                    }
                    else
                    {
                        jsonObject.put("paytype",patientkey.getPatientPaytype());
                    }
                }
                else
                {
                    jsonObject.put("paytype","未知");
                }


                //jsonObject.put("id",patientkey.getIdPatient());
                //jsonObject.put("id",patientkey.getIdPatient());
                //jsons.add(jsonObject);
                jsons.put(jsonObject);
            }



        }
        response.getWriter().print(jsons.toString());
    }



    @RequestMapping (value = "/ajax/post_patient_info", method=RequestMethod.POST)
    @ResponseBody
    public String waiting_patient_info(@RequestBody String patient_info ) throws Exception
    {
        System.out.println("begin_post_knowledge");
       // System.out.println(patient_info);

        JSONObject jb=new JSONObject(patient_info);
        //将json格式的字符串转换为json对象，并取得该对象的“userName”属性值
        String clinicId=jb.getString("clinicId");
        //String m=jb.getString("name");
        List<PatientEntity>   patientEntityList=patientRepository.findAll();
        for(PatientEntity patientKey: patientEntityList)
        {
            if((patientKey.getPatientClinicId().trim()).equals(clinicId.trim()))
            {
                patientKey.setPatientClinicState("1");
                patientRepository.saveAndFlush(patientKey);
            }
        }

        System.out.println("finish_post_knowledge");
        return "";
    }





    @RequestMapping (value = "/ajax/finish_diagnosis", method=RequestMethod.POST)
    @ResponseBody
    public String finish_diagnosis(@RequestBody String order_info ) throws Exception
    {
        System.out.println("begin_post_patient_record");
        //System.out.println(patient_info);

        JSONObject jb=new JSONObject(order_info);
        //将json格式的字符串转换为json对象，并取得该对象的“userName”属性值
        String clinicId=jb.getString("clinicId");
        String drug=jb.getString("drug");
        //String m=jb.getString("name");
        System.out.println("drug is"+drug);

        List<PatientEntity>   patientEntityList=patientRepository.findAll();
        for(PatientEntity patientKey: patientEntityList)
        {
            if(patientKey.getPatientClinicId().trim().equals(clinicId.trim()))
            {
                patientKey.setPatientClinicState("3");
                patientRepository.saveAndFlush(patientKey);
            }
        }

        List<PatientRecordEntity> patientRecordEntityList=patientRecordRepository.findAll();
        for(PatientRecordEntity patientRecordEntity:patientRecordEntityList)
        {
            if(patientRecordEntity.getPatientRecordState()==2)
            {
                patientRecordEntity.setPatientRecordDrug(drug.trim());
                patientRecordEntity.setPatientRecordState(1);
                patientRecordRepository.saveAndFlush(patientRecordEntity);
            }
        }

        System.out.println("finish_diagnosis");
        return "";
    }

    @RequestMapping (value = "/ajax/save_patient_record", method=RequestMethod.POST)
    @ResponseBody
    public String Save_Patient_Record(@RequestBody String patientrecord ) throws Exception
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        JSONObject jb=new JSONObject(patientrecord);

        //将json格式的字符串转换为json对象，并取得该对象的“userName”属性值
        String clinicid=jb.getString("clinicid");

        String symptom=jb.getString("symptom");
        //System.out.println("Symptom is"+symptom);

        String diagnosis=jb.getString("firstdiagnosis");
        String history_disease=jb.getString("history_disease");
        //String name=jb.getString("name");

       PatientRecordEntity patientRecordEntity=new PatientRecordEntity();
        patientRecordEntity.setClinicid(clinicid);
        patientRecordEntity.setPatientRecordSymptom(symptom);
        patientRecordEntity.setPatientRecordDiagnosis(diagnosis);
        //patientRecordEntity.setPatientRecord;
        patientRecordEntity.setPatientRecordState(2);
        patientRecordEntity.setPatientRecordTime(df.format(new Date()));
        patientRecordRepository.saveAndFlush(patientRecordEntity);

        System.out.println("finish_save_record");
        return "";
    }


    @RequestMapping("/post_symptom_metadata"  )
    public void post_sym_metadata(HttpServletRequest request, HttpServletResponse response) throws Exception  {

        List<SymptomEntity> symptomEntityList=symptomRepository.findAll();
        List<DiagnosisEntity> diagnosisEntityList=diagnosisRepository.findAll();
        List<PhysicalExamEntity> physicalExamEntityList=physicalExamRepository.findAll();
        List<InstrumentExamEntity> instrumentExamEntityList=instrumentRepository.findAll();
        List<DrugEntity> drugEntityList=drugRepository.findAll();
        JSONArray jsons_symptom=new JSONArray();
        for(SymptomEntity symptomEntity:symptomEntityList)
        {
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("id",symptomEntity.getIdSymptom());
            jsonObject.put("name",symptomEntity.getSymptomName());
            jsons_symptom.put(jsonObject);
        }
        response.getWriter().print(jsons_symptom.toString());
    }


    @RequestMapping("/post_diagnosis_metadata"  )
    public void post_diagnosis_metadata(HttpServletRequest request, HttpServletResponse response) throws Exception  {


        List<DiagnosisEntity> diagnosisEntityList=diagnosisRepository.findAll();

        //List<DrugEntity> drugEntityList=drugRepository.findAll();
        JSONArray jsons=new JSONArray();
        for(DiagnosisEntity diagnosisEntity:diagnosisEntityList)
        {
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("id",diagnosisEntity.getIddiagnosis());
            jsonObject.put("name",diagnosisEntity.getDiagnosisName());
            jsons.put(jsonObject);
        }
        response.getWriter().print(jsons.toString());
    }


    @RequestMapping("/post_phy_exam_metadata"  )
    public void post_phy_exam_metadata(HttpServletRequest request, HttpServletResponse response) throws Exception  {


        //List<DiagnosisEntity> diagnosisEntityList=diagnosisRepository.findAll();
        List<PhysicalExamEntity> physicalExamEntityList=physicalExamRepository.findAll();
        //List<DrugEntity> drugEntityList=drugRepository.findAll();
        JSONArray jsons=new JSONArray();
        for(PhysicalExamEntity physicalExamEntity:physicalExamEntityList)
        {
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("id",physicalExamEntity.getIdphysicalExam());
            jsonObject.put("Content",physicalExamEntity.getPhysicalExamContent());
            jsonObject.put("Value",physicalExamEntity.getPhysicalExamValue());
            jsonObject.put("organ",physicalExamEntity.getPhysicalExamOrgan());
            jsons.put(jsonObject);
        }
        response.getWriter().print(jsons.toString());
    }

    @RequestMapping("/post_instru_exam_metadata"  )
    public void post_instru_exam_metadata(HttpServletRequest request, HttpServletResponse response) throws Exception  {


        //List<DiagnosisEntity> diagnosisEntityList=diagnosisRepository.findAll();
        List<InstrumentExamEntity> instrumentExamEntityList=instrumentRepository.findAll();
        //List<DrugEntity> drugEntityList=drugRepository.findAll();
        JSONArray jsons=new JSONArray();
        for(InstrumentExamEntity instrumentExamEntity:instrumentExamEntityList)
        {
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("id",instrumentExamEntity.getIdinstrumentExam());
            jsonObject.put("Content",instrumentExamEntity.getInstrumentExamContent());
            jsonObject.put("Value",instrumentExamEntity.getInstrumentExamValue());
            jsons.put(jsonObject);
        }
        response.getWriter().print(jsons.toString());
    }


    @RequestMapping("/post_drug_metadata"  )
    public void post_drug_metadata(HttpServletRequest request, HttpServletResponse response) throws Exception  {


        //List<DiagnosisEntity> diagnosisEntityList=diagnosisRepository.findAll();
        //List<PhysicalExamEntity> physicalExamEntityList=physicalExamRepository.findAll();
        List<DrugEntity> drugEntityList=drugRepository.findAll();
        JSONArray jsons=new JSONArray();
        for(DrugEntity drugEntity:drugEntityList)
        {
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("id",drugEntity);
            jsonObject.put("drug_name",drugEntity.getDrugname());
            jsonObject.put("drug_spec",drugEntity.getDrugspec());
            jsonObject.put("drug_produce_company",drugEntity.getDrugProduceCompany());
            jsonObject.put("drug_per_prize",drugEntity.getDrugPerPrize());
            jsons.put(jsonObject);
            System.out.println(drugEntity.getDrugname());
        }
        response.getWriter().print(jsons.toString());
    }


}
